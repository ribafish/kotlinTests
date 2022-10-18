import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json =  Json {
    ignoreUnknownKeys = true
    classDiscriminator = "event"
}

fun main() {
    testClassDiscriminator()
}

@Serializable
abstract class EdaEvent {
    abstract val payload: EdaPayload
}
@Serializable
abstract class EdaPayload

@Serializable object EmptyPayload: EdaPayload()

@Serializable
@SerialName("inventory-stock-inquiry")
data class InventoryStockInquiryRequest(
    override val payload: EmptyPayload = EmptyPayload
) : EdaEvent()

@Serializable
@SerialName("inventory-stock")
data class InventoryStockInquiryResponse (
    override val payload: InventoryStockInquiryResponsePayload) : EdaEvent()
@Serializable
data class InventoryStockInquiryResponsePayload(val inventoryStock: Map<String, Int>): EdaPayload()


fun testClassDiscriminator() {
    val responseData = "{\"event\":\"inventory-stock\",\"payload\":{\"inventoryStock\":{\"bananas\":346104,\"lemons\":276892,\"apples\":15,\"pineapples\":5}}}"

    val request = InventoryStockInquiryRequest()
//    val reqStr = json.encodeToString(request as EdaEvent)
//    println("request:$reqStr")

    val response = json.decodeFromString<EdaEvent>(responseData)
    println("response:$response")
}
