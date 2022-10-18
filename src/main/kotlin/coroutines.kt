import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.system.exitProcess
import kotlin.system.measureTimeMillis

fun main() {
    testSharingFlow()
}


val f1 = flowOf(1,3,4).onEach { delay(100) }
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
