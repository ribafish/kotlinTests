import com.google.gson.Gson


fun main() {
    val enabledFeatures = generateMap().map { (serial, features) ->
        val featureItems = features.map { feature ->
            FeatureItem(featureCode = feature, expiry = 0L, signature = "")
        }
        serial to featureItems
    }.toMap()

    println(Gson().toJson(enabledFeatures))
}

data class EnabledFeatures(val unleashedFeatures: Map<String, List<FeatureItem>>)

data class FeatureItem(val featureCode: Int, val expiry: Long, val signature: String)


private fun generateMap() = mapOf(
    "000025" to listOf(Feature.NAME_LIST, Feature.NAME_LIST_QR_CODE_VIEWER),
    "000050" to listOf(Feature.NAME_LIST),
    "000055" to listOf(Feature.NAME_LIST),
    "000100" to listOf(Feature.NAME_LIST, Feature.NAME_LIST_QR_CODE_VIEWER),
    "000101" to listOf(Feature.NAME_LIST, Feature.NAME_LIST_QR_CODE_VIEWER),
    "000102" to listOf(Feature.NAME_LIST),
    "000530" to listOf(Feature.NAME_LIST),
    "000531" to listOf(Feature.NAME_LIST),
    "000532" to listOf(Feature.NAME_LIST),
    "000540" to listOf(Feature.NAME_LIST),
    "000541" to listOf(Feature.NAME_LIST),
    "000542" to listOf(Feature.NAME_LIST),
    "000543" to listOf(Feature.NAME_LIST),
    "000612" to listOf(Feature.NAME_LIST),
    "000613" to listOf(Feature.NAME_LIST),
    "000614" to listOf(Feature.NAME_LIST),
    "000620" to listOf(Feature.NAME_LIST),
    "000621" to listOf(Feature.NAME_LIST),
    "000622" to listOf(Feature.NAME_LIST),
    "000623" to listOf(Feature.NAME_LIST),
    "000624" to listOf(Feature.NAME_LIST),
    "000647" to listOf(Feature.NAME_LIST),
    "000648" to listOf(Feature.NAME_LIST),
    "000649" to listOf(Feature.NAME_LIST),
    "000687" to listOf(Feature.NAME_LIST),
    "000757" to listOf(Feature.NAME_LIST_QR_CODE_VIEWER)
)

object Feature {
    const val NAME_LIST = 1
    const val NAME_LIST_QR_CODE_VIEWER = 2

    const val MOTION_CONTROL_SYNC = 21
    const val VGA_PREVIEWS = 22
    const val FHD_PREVIEWS = 23
}
