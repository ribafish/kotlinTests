import kotlinx.coroutines.InternalCoroutinesApi

/**
 * You can edit, run, and share this code.
 * play.kotlinlang.org
 */


@InternalCoroutinesApi
fun main() {
    println("Hello, world!!!")
    println("Getting GITHUB_ACTIONS env var: ${System.getenv("GITHUB_ACTIONS")}")
    val envVars = System.getenv().entries.map { (key, value) ->
        if (key.contains("key", ignoreCase = true) || key.contains("token", ignoreCase = true))
            "\t$key=***"
        else
            "\t$key=$value"
    }.reduce { acc, it -> "$acc\n$it" }
    println("Available env vars:\n$envVars\n")

    quickChecks()


//    coroutinesTest()
//    rxTests()
//    coroutinesAsyncTest()
}
