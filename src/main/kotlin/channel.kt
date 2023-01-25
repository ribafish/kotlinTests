import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlin.system.exitProcess

fun main() {
//    test1()
    test2()
}

data class State(val transactions: List<Int>, val state: String)

var transactionsChannel: Channel<List<Int>>? = null

var syncStateChannel: Channel<String>? = null

fun getTranCh(): Channel<List<Int>> {
    transactionsChannel?.let { return it }
    return Channel<List<Int>>()
        .also {
            it.invokeOnClose {
                println("TranCh close: $it")
                transactionsChannel = null
            }
            transactionsChannel = it
            it.trySend(listOf(1))
        }
}

fun getSyncCh(): Channel<String> {
    syncStateChannel?.let { return it }
    return Channel<String>()
        .also {
            it.invokeOnClose {
                println("SyncCh close: $it")
                syncStateChannel = null
            }
            syncStateChannel = it
            it.trySend("a")
        }
}

//    fun observeTransactions() = getTranCh().receiveAsFlow()
//    fun observeSyncState() = getSyncCh().receiveAsFlow()
//
//    suspend fun sendTran(i: Int) {
//        println("Sending transaction: $i")
//        getTranCh().send(i)
//    }
//
//    suspend fun sendSync(s: String) {
//        println("Sending SyncState: $s")
//        getSyncCh().send(s)
//    }

val tranFlow = MutableSharedFlow<List<Int>>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
val syncFlow = MutableSharedFlow<String>(1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

fun observeTransactions() = tranFlow
fun observeSyncState() = syncFlow

suspend fun sendTran(iList: List<Int>) {
    println("Sending transactions: $iList")
    tranFlow.emit(iList)
}

suspend fun sendSync(s: String) {
    println("Sending SyncState: $s")
    syncFlow.emit(s)
}

fun getStateFlow(): Flow<State> {
    return observeSyncState().combine(observeTransactions()) { syncState, transactions ->
        State(transactions, syncState)
    }
        .distinctUntilChanged()
}

suspend fun CoroutineScope.collectStateFlow(): Job {
    println("collectStateFlow")
    return launch(Dispatchers.Unconfined) {
        getStateFlow()
            .onCompletion {
                println("Completion: $it")
            }
            .onEmpty {
                println("Empty")
            }
            .onStart {
                println("Start")
            }
            .catch {
                println("Error: $it")
            }
            .collect {
                println("Collect $it")
            }
    }
}

fun test1() {
    runBlocking {

        val j1 = collectStateFlow()

        sendTran(listOf(1, 2))
        sendSync("b")

        sendTran(listOf(1, 2, 3))

        println("j1 cancel")
        j1.cancel()

        sendSync("c")
        sendSync("d")

        println("-------------")
        val j2 = collectStateFlow()

        sendTran(listOf(1, 2, 3, 4))
        sendSync("e")

        sendSync("f")


        sendTran(listOf(1, 2, 3, 4, 5))
        sendTran(listOf(1, 2, 3, 4, 5, 6))
        sendTran(listOf(1, 2, 3, 4, 5, 6))
        sendTran(listOf(1, 2, 3, 4, 5, 6))

        j2.cancel()


        delay(100)
        exitProcess(0)
    }
}

fun test2() {
    val ch = Channel<Int>(capacity = 0, onBufferOverflow = BufferOverflow.DROP_OLDEST).also { it.trySend(0) }
    val state = MutableStateFlow(0)
    val shared = MutableSharedFlow<Int>(replay = 0,  extraBufferCapacity = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST).also { it.tryEmit(0) }

    fun send(i: Int) {
        println("Sending $i")
        ch.trySend(i)
        state.tryEmit(i)
        shared.tryEmit(i)
    }

    suspend fun CoroutineScope.collectAll(): Job {
        println("collectAll")
        return launch(Dispatchers.Unconfined) {
            listOf(
                ch.receiveAsFlow() to "Channel",
                state to "StateFlow",
                shared to "SharedFlow"
            ).forEach { (flow, name) ->
                launch {
                    flow.onCompletion {
                        println("$name -> Completion: $it")
                    }
                        .onEmpty {
                            println("$name -> Empty")
                        }
                        .onStart {
                            println("$name -> Start")
                        }
                        .catch {
                            println("$name -> Error: $it")
                        }
                        .collect {
                            println("$name -> Collect $it")
                        }
                }
            }
        }
    }

    runBlocking {
        val j1 = collectAll()

        send(1)
        send(2)

        j1.cancel()

        send(3)
        send(4)

        val j2 = collectAll()

        send(5)
        send(6)

        j2.cancel()

        send(7)
    }

}
