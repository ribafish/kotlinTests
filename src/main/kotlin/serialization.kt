import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeParseException
import java.math.BigDecimal

private val json =  Json {
    ignoreUnknownKeys = true
    classDiscriminator = "event"
    serializersModule = SerializersModule {
        contextual(ZonedDateTime::class, ZonedDateTimeSerializer)
        contextual(BigDecimal::class, BigDecimalSerializer)
        contextual(TestInterface::class, TestInterfaceSerializer)
        polymorphic(baseClass = TestInterface::class) {
            subclass(TestInterfaceImpl::class)
        }
        polymorphic(baseClass = TagElement::class) {
            subclass(Tag::class)
            subclass(TagFolder::class)
        }
    }
}
val gson: Gson = GsonBuilder()
    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    .registerTypeAdapter(ZonedDateTime::class.java, ZonedDateTimeTypeAdapter())
    .create()

fun main() {
//    testZonedDateTime()
//    testBigDecimal()
//    testEnums()
//    testOmitNulls()
//    testPolymorphicDiscriminator()
//    testPiiModel()
//    testLists()
//    testAfp()
//    testErrorBody()
    testInterfaces()
}

sealed class TagElement
@Serializable
data class TagFolder(val title: String, val list: List<@Polymorphic TagElement>): TagElement()
@Serializable
data class Tag(val title: String, val is_done: Boolean, val code: String): TagElement()


fun testInterfaces() {
    val tags = TagFolder("first",
        listOf(
            TagFolder("2nd", listOf(Tag("tag1", false, "MIXED"))),
            Tag("tag2", true, "MIXED")))

    val tagsString = json.encodeToString(tags)
    println(tagsString)

    val tags2 = json.decodeFromString<TagFolder>(tagsString)
    println(tags2)
}


fun testErrorBody() {
    val serial = json.encodeToString(ErrorBody(0,"msg","err")).also { println(it) }
    val serial3 = json.encodeToString(ErrorBody(0,null,"err")).also { println(it) }
    val serial4 = json.encodeToString(ErrorBody(0,"msg",null)).also { println(it) }
    val serial5 = json.encodeToString(ErrorBody(0,null,null)).also { println(it) }

    println("Deserialized: ${json.decodeFromString<ErrorBody>(serial)}")
    println("Deserialized3: ${json.decodeFromString<ErrorBody>(serial3)}")
    println("Deserialized4: ${json.decodeFromString<ErrorBody>(serial4)}")
    println("Deserialized5: ${json.decodeFromString<ErrorBody>(serial5)}")


    val serial2 = """{"status_code":0,"message":"msg","error_code":"err"}""".also { println(it) }
    println("Deserialized2: ${json.decodeFromString<ErrorBody>(serial2)}")
}

@Serializable(with = ErrorBodySerializer::class)
//@Serializable
data class ErrorBody(
    val statusCode: Int,
    val message: String? = null,
    val errorCode: String? = null,
)

object ErrorBodySerializer: KSerializer<ErrorBody> {

    override val descriptor: SerialDescriptor
        get() = JsonPrimitive.serializer().descriptor

    override fun deserialize(decoder: Decoder): ErrorBody {
        val json = (decoder as JsonDecoder).decodeJsonElement().jsonObject
        return ErrorBody(
            statusCode = (json[STATUS_CODE] ?: json[STATUS_CODE_SNAKE])!!.jsonPrimitive.int,
            message = json[MESSAGE]?.jsonPrimitive?.content,
            errorCode = json[ERROR_CODE]?.jsonPrimitive?.content
        )
    }

    override fun serialize(encoder: Encoder, value: ErrorBody) {
        val json = buildJsonObject {
            put(STATUS_CODE, value.statusCode)
            value.message?.let { put(MESSAGE, it) }
            value.errorCode?.let{ put(ERROR_CODE, it) }
        }
        (encoder as JsonEncoder).encodeJsonElement(json)
    }

    private const val STATUS_CODE = "statusCode"
    private const val STATUS_CODE_SNAKE = "status_code"
    private const val MESSAGE = "message"
    private const val ERROR_CODE = "error_code"
}

@Serializable
data class FileValue(
    @SerialName("file_uuid")
    val fileUuid: String,
    @SerialName("file_password")
    val filePassword: String)

fun testAfp() {
    println("Test1")
    val mappedData = mapOf<String, String>("file" to json.encodeToString(FileValue("uuid", "password")), "string" to "stringValue")

    val selfDeclared = mappedData.mapValues{ d -> d.value }

    val regular = json.encodeToString(mappedData)

    println("selfDeclared: $selfDeclared")
    println("regular: $regular")
    testAfp2()
}

fun testAfp2() {
    println("\n\nTest2")
    val mappedData = mapOf("file" to json.encodeToJsonElement(FileValue("uuid", "password")), "string" to JsonPrimitive("stringValue"))

    val selfDeclared = mappedData.mapValues{ d -> d.value.let {
        if (it is JsonPrimitive) {
            it.content
        } else {
            it.toString()
        }
    } }

    val regular = json.encodeToString(mappedData)

    println("selfDeclared: $selfDeclared")
    println("regular: $regular")
}


fun testLists() {
    val list = listOf(1,2,3,4)
    val serial = json.encodeToString(list)
    val serialJson = JsonObject(mapOf("0" to json.encodeToJsonElement(list)))

    val deserial = json.decodeFromString<List<Int>>(serial)
    val deserialJson = json.decodeFromJsonElement<List<Int>>(serialJson["0"]!!.jsonArray)
    val deserialJson2 = parsePayloadArray<Int>(serialJson)

    println("deserial: $deserial")
    println("deserialJson: $deserialJson")
    println("deserialJson2: $deserialJson2")

}
private inline fun <reified T> parsePayloadArray(message: JsonObject): List<T> {
    return json.decodeFromJsonElement(message["0"]!!.jsonArray)
}

//@Serializable
//class PiiModel: HashMap<String, String>()
typealias PiiModel = HashMap<String, String>

fun testPiiModel() {
    val data = """{"5842f89a-85a9-483e-8b85-9ab6b2b50261":"SI","c8873645-54f7-4526-8fb3-7a17b79cc04f":"+38641277147","6310e764-cd12-47b1-963d-3ddf027aa325":"8"}"""

    val decoded = json.decodeFromString<PiiModel>(data)

    val piiModel = PiiModel(decoded)

    println("PiiModel: $decoded")
}

interface TestInterface

@Serializable
data class TestInterfaceImpl(val data: String): TestInterface

@Serializable
data class TestInterfaceImplHolder(
    @Serializable(with = TestInterfaceSerializer2::class)
                                   val data: TestInterface)

object TestInterfaceSerializer : JsonContentPolymorphicSerializer<TestInterface>(TestInterface::class) {
    override fun selectDeserializer(element: JsonElement) = TestInterfaceImpl.serializer()
}

object TestInterfaceSerializer2: JsonTransformingSerializer<TestInterface>(PolymorphicSerializer(TestInterface::class)) {
    override fun transformSerialize(element: JsonElement): JsonElement {
        return JsonObject(element.jsonObject.filterNot { it.key == "type" })
    }
}

fun testPolymorphicDiscriminator() {
    val holder = TestInterfaceImplHolder(TestInterfaceImpl("stuff"))

    println(json.encodeToString(holder))
}

@Serializable
data class OmitNullsClass(val requiredVal: Int, val optionalValSet: Int?, val optionalValDefault: Int? = null)

fun testOmitNulls() {
    val classes = listOf(
        OmitNullsClass(0, 0),
        OmitNullsClass(1, 1, null),
        OmitNullsClass(2, null, 2),
        OmitNullsClass(3, null, null),
    )
    classes.forEach {
        println("Serialized: `${json.encodeToString(it)}`")
    }

    val encodedClasses = json.encodeToString(classes)
    println("Deserialized: `${json.decodeFromString<List<OmitNullsClass>>(encodedClasses)}`")
}

@Serializable
enum class EnumTest {
    @SerialName("first")
    FIRST,
    @SerialName("second")
    SECOND_SECOND
}

@Serializable
data class EnumTestClass(val test: EnumTest)

fun testEnums() {
    val data = listOf(EnumTestClass(EnumTest.FIRST), EnumTestClass(EnumTest.SECOND_SECOND))
    println("Kotlin enum -> json: ${json.encodeToString(data)}")
}

@Serializable
data class BigDecimalWrapper(@Contextual val value: BigDecimal)

fun testBigDecimal() {
    val thing = BigDecimalWrapper(BigDecimal("10.12"))

    println("bigDecimal: toString: ${thing.value.toString()} \n\t\ttoPlainString ${thing.value.toPlainString()}\n  toEngineeringString ${thing.value.toEngineeringString()}")

    val gsonEnc = gson.toJson(thing)
    val kotlinEnc = json.encodeToString(thing)

    println("gson: \t\t'$gsonEnc'")
    println("kotlinx: \t'$kotlinEnc'")

    val gsonParsed = gson.fromJson(gsonEnc, BigDecimalWrapper::class.java)
    val kotlinParsed = json.decodeFromString<BigDecimalWrapper>(kotlinEnc)
    println("gson encode -> gson parse: \t\t${gsonParsed}")
    println("kotlin encode -> kotlin parse: \t${kotlinParsed}")

    println("kotlin encode -> gson parse: ${gson.fromJson(kotlinEnc, BigDecimalWrapper::class.java)}")
    println("gson encode -> kotlin parse: ${json.decodeFromString<BigDecimalWrapper>(gsonEnc)}")

//    require(gson.toJson(thing).equals(json.encodeToString(thing)))
}

@Serializer(forClass = BigDecimal::class)
object BigDecimalSerializer: KSerializer<BigDecimal> {
    override val descriptor: SerialDescriptor
        get() = JsonPrimitive.serializer().descriptor

    override fun serialize(output: Encoder, obj: BigDecimal) {
        (output as JsonEncoder).encodeJsonElement(JsonPrimitive(obj))
    }

    override fun deserialize(input: Decoder): BigDecimal {
        val encodedBigDecimal = (input as JsonDecoder).decodeJsonElement().jsonPrimitive.content
        return BigDecimal(encodedBigDecimal)
    }
}

fun testZonedDateTime() {
    val identity = Identity("name", "status", ZonedDateTime.ofInstant(Instant.now(), ZoneOffset.UTC), null)

    println("kotlinx: '${json.encodeToString(identity)}'")
    println("gson: '${gson.toJson(identity)}'")

    val encoded = """{"gid_name":"name","status":"status","created_at":"2021-07-27T06:45:51.458Z","newField":"thing"}"""
    println("kotlinx: ${json.decodeFromString<Identity>(encoded)}")
    println("gson: ${gson.fromJson(encoded, Identity::class.java)}")

    val encoded2 = """{"gid_name":"name","status":"status","created_at":null,"newField":"thing"}"""
    println("kotlinx: ${json.decodeFromString<Identity>(encoded2)}")
    println("gson: ${gson.fromJson(encoded2, Identity::class.java)}")
}

@Serializable
private data class Identity(
    @SerialName("gid_name")
    val gidName: String,
    val status: String,
    @SerialName("created_at")
    @Contextual
    val createdAt: ZonedDateTime?,
    val thing: String? = null)

object ZonedDateTimeSerializer: KSerializer<ZonedDateTime> {

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("ZonedDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ZonedDateTime) {
        ApiDateTimeUtil.createZonedDateTimeString(value, longFormat = true)?.let {
            encoder.encodeNotNullMark()
            encoder.encodeString(it)
        } ?: encoder.encodeNull()
    }

    override fun deserialize(decoder: Decoder): ZonedDateTime {
        val string = decoder.decodeString()
        return ApiDateTimeUtil.parseZonedDateTimeString(string)!!
//        return if (decoder.decodeNotNullMark()) {
//            val string = decoder.decodeString()
//            ApiDateTimeUtil.parseZonedDateTimeString(string)
//        } else {
//            decoder.decodeNull()
//        }
    }
}

class ZonedDateTimeTypeAdapter : TypeAdapter<ZonedDateTime>() {

    override fun write(out: JsonWriter, value: ZonedDateTime?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(ApiDateTimeUtil.createZonedDateTimeString(value, longFormat = true))
        }
    }

    override fun read(inReader: JsonReader?): ZonedDateTime? {
        return if (inReader?.peek() === JsonToken.NULL) {
            inReader.nextNull()
            null
        } else {
            val date = inReader?.nextString()
            ApiDateTimeUtil.parseZonedDateTimeString(date)
        }
    }
}

object ApiDateTimeUtil {
    private val FORMAT_LONG = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private val FORMAT_SHORT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")

    fun createZonedDateTimeString(dateTime: ZonedDateTime?, longFormat: Boolean = true): String? {
        val formatter = if (longFormat) FORMAT_LONG else FORMAT_SHORT
        return dateTime?.withZoneSameInstant(ZoneOffset.UTC)?.format(formatter)
    }

    fun parseZonedDateTimeString(dateTimeString: String?): ZonedDateTime? {
        return try {
            parseZonedDateTimeString(dateTimeString, true)
        } catch (e: DateTimeParseException) {
            // in some cases, milliseconds are not returned, try to parse with different format
            try {
                parseZonedDateTimeString(dateTimeString, false)
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }

    fun parseZonedDateTimeLong(milliseconds: Long): ZonedDateTime {
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(milliseconds), ZoneOffset.UTC)
    }

    private fun parseZonedDateTimeString(dateTimeString: String?, longFormat: Boolean = true): ZonedDateTime? {
        return dateTimeString?.let {
            val formatter = if (longFormat) FORMAT_LONG else FORMAT_SHORT
            LocalDateTime.parse(it, formatter).atZone(ZoneOffset.UTC)
        }
    }
}
