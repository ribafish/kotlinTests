import kotlinx.coroutines.InternalCoroutinesApi

/**
 * You can edit, run, and share this code.
 * play.kotlinlang.org
 */


@InternalCoroutinesApi
fun main() {
    println("Hello, world!!!")
    println("Getting GITHUB_ACTIONS env var: ${System.getenv("GITHUB_ACTIONS")}")
    println(System.getenv())

    quickChecks()


//    coroutinesTest()
//    rxTests()
//    coroutinesAsyncTest()
}
