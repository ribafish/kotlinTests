import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis
import kotlin.time.Duration.Companion.seconds

fun main() {
//    testSharingFlow()
//    checkWithPrevious()
//    testSuspendCoroutine()
//    testSuspendCoroutine2()
//    testFlowDispatchers()
//    testCallbackFlow()
    testCancellation()
}


val f1 = flowOf(1, 3, 4).onEach { delay(100) }
    .flowOn(Dispatchers.Default)
val f2 = flowOf("a", "b", "c").onEach { delay(150) }
    .flowOn(Dispatchers.Default)

fun flowTest() = runBlocking {
    println("Coroutines test")
//    f1.combine(f2) { a,b ->
//        a to b
//    }.collect {
//        println(it)
//    }

    combine(f1, f2) { a, b ->
        a to b
    }.collect {
        println(it.component1())
    }
}

fun coroutinesAsyncTest() = runBlocking {
    val list = 1..100

    val time = measureTimeMillis {
        val sum = list.map {
            delay(10L)
            it
        }.reduce { acc, i -> acc + i }
        println("sum $sum")
    }
    println("Completed in $time ms")

    val time2 = measureTimeMillis {
        val sum = list.map {
            async {
                delay(10L)
                it
            }
        }.awaitAll().reduce { acc, i -> acc + i }
        println("sum $sum")
    }
    println("Completed in $time2 ms")
}

fun testSharingFlow(): Nothing = runBlocking {
    println("\n--------------------------")
    println("testSharingFlow")
    println("--------------------------\n")
//    val _flow = MutableSharedFlow<String>(replay = 64)
//    val flow = _flow.onEach {
//        _flow.resetReplayCache()
//    }
    val channel = Channel<String>(capacity = 64)
    val flow = channel.receiveAsFlow()

    suspend fun collect() = launch(Dispatchers.Default) {
        flow.collect {
            println("Collected $it")
        }
    }

    suspend fun emit5(dif: String) {
        println("Emit 5 -> $dif")
        for (i in 1..5) {
            delay(100)
            channel.send("$dif $i")
//            _flow.emit("$dif $i")
        }
    }

    emit5("A")

    val job1 = collect()
//    emit5("B")
    delay(100)
    job1.cancel()

    emit5("C")

    val job2 = collect()
    emit5("D")
    job2.cancel()


    delay(100)
    exitProcess(0)
}

fun testSuspendCoroutine() {
    println("\n--------------------------")
    println("testSuspendCoroutine")
    println("--------------------------\n")
    suspend fun doStuff(i: Int, mapper: (Int) -> String) =
        suspendCoroutine { continuation ->
            continuation.resume(mapper(i))
        }

    suspend fun initStuff() {
        doStuff(1) {
            throw IllegalArgumentException("WOoo")
        }
    }



    runBlocking {
        try {
            initStuff()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun testSuspendCoroutine2() {
    runBlocking {
        val i = withTimeout(3.seconds) {
            suspendCoroutine { continuation ->
                runBlocking {
                    delay(5_000)
                    continuation.resume(1)
                }
            }
        }
        println("i=$i")
    }
}

fun checkWithPrevious() {
    fun <T : Any> Flow<T>.withPrevious(): Flow<Pair<T?, T>> = flow {
        var prev: T? = null
        this@withPrevious.collect {
            emit(prev to it)
            prev = it
        }
    }

    println("\n--------------------------")
    println("checkWithPrevious")
    println("--------------------------\n")
    val flow = flow {
        for (i in 1..10) {
            println("emit $i")
            emit(i)
        }
    }

    runBlocking {
        flow
            .withPrevious()
            .map { (it.first ?: 0) + it.second }
            .collect {
                println("Collect $it")
            }
    }
}

fun testFlowDispatchers() = runBlocking {
    fun Thread.printThread(msg: String): Thread = this.also { println("Doing '$msg' on thread id $id -> $name") }
    Thread.currentThread().printThread("runBlocking")

    suspend fun getCurrentThreadId(): Long {
        delay(200)
        return Thread.currentThread().let {
            it.printThread("getCurrentThreadId").id
        }
    }

    val flow = flow {
        println("Flow start")
        getCurrentThreadId()
        println("Doing emit..")
        emit(getCurrentThreadId())
        println("Flow end")
    }

    val currentThreadId = getCurrentThreadId()

    suspend fun Flow<Long>.collectAndCheck(msg: String) = this.collect {
        println("Collect emit with '$msg', thread id is main/current thread: ${it == currentThreadId}")
    }

    println("\n--------------------------")
    println("Starting collections")
    println("--------------------------\n")

    flow.collectAndCheck("just collect")
    println("--------------------------\n")
    flow.flowOn(Dispatchers.Unconfined).collectAndCheck("flowOn Unconfined")
    println("--------------------------\n")
    withContext(Dispatchers.Default) {
        flow.collectAndCheck("withContext Default")
    }
    println("--------------------------\n")
    withContext(Dispatchers.Default) {
        flow.flowOn(Dispatchers.Unconfined).collectAndCheck("withContext Default + flowOn Unconfined")
    }
}

fun testCallbackFlow() = runBlocking {
    val f1 = callbackFlow<Int> {
        awaitClose { println("Close callback flow") }
    }
    val f = flow {
        emit(1)
        emitAll(f1)
    }

    try {
        withTimeout(5000) {
            f.collect {
                println("collect: $it")
            }
        }
    } catch (e: TimeoutCancellationException) {
        println("Cancelled after 5s")
    }
}


fun testCancellation() = runBlocking {
    suspend fun runLongOp() {
        delay(500)
        println("Got to the end after 500ms")
    }

    println("Starting job1")
    val j1 = launch {
        runLongOp()
    }

    delay(200)

    j1.cancel()
    println("Cancelled job1 after 200ms")

    delay(500)

    println("-----------------")
    println("Starting job2")

    val j2 = launch {
        runBlocking {
            runLongOp()
        }
    }

    delay(200)

    j2.cancel()
    println("Cancelled job2 after 200ms")

    delay(500)
    println("End test after additional 500ms")


}
