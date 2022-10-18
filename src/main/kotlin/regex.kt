
fun main() {

    testParseVersion()
}

fun testParseVersion() {
    val names = listOf(
        "wr1_json 6.35",
        "wr1_json6.35",
        "wr1_json   6.35",
        "wr1_json   6.34.json",
        "6.35",
        "wr1_json   6.3",
        "wr1_json   6.3",
        "wr1_json   342346.3234324",
        "wr1_json   342346.",
        "wr1_json   342346",
        "wr1_json 6 35"
    )

    println(names.map { parseVersion(it) })
}

fun parseVersion(content: String): Pair<Int, Int>? {
    val versionString = "\\d+\\.\\d+".toRegex().find(content)?.groupValues?.firstOrNull()
    return versionString?.split(".")?.let { majorMinor ->
        majorMinor.first().toInt() to majorMinor.last().toInt()
    }
}
