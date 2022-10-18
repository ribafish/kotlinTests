import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and

fun main() {
    quickChecks()
}

fun quickChecks() {
//    check1()

//    testHexFormat()
//    checkNulls()
//    flatMapOnMap()
//    testChangeListInPlace()
//    extendableEnums()
//    compareTwosComplement()
//    checkBooleanRun()
//    checkComparable()
//    checkSplit()
    checkByteArrayAppend()
}

fun checkByteArrayAppend() {
    fun ByteArray.append(data: ByteArray?): ByteArray {
        return data?.let { data ->
            val buffer = ByteBuffer.allocate(size + data.size)
            iterator().forEach {
                buffer.put(it)
            }
            data.iterator().forEach {
                buffer.put(it)
            }
            buffer.array()
        } ?: this
    }

    fun ByteArray.asString(): String {
        val list = mutableListOf<Byte>()
        iterator().forEach {
            list.add(it)
        }
        val bytesString = list.map { String.format(Locale.ROOT, "%02x", it) }
        return "ByteArray x$bytesString"
    }

    val ba1 = ByteBuffer.allocate(3).put(1).put(2).put(3).array()
    val ba2 = ByteBuffer.allocate(3).put(4).put(5).put(6).array()

    val ba3 = ba1.append(ba2)
    val ba4 = ba1.append(null)
    val ba5 = ba2.append(ba1)

    println(ba3.asString())
    println(ba4.asString())
    println(ba5.asString())
}

fun checkBooleanRun() {
    println("checkBooleanRun")
    val isCheck = true && runCheck()
    val isCheck2 = false && runCheck()
    println("checkBooleanRun isCheck $isCheck isCheck2 $isCheck2")
}

fun runCheck(): Boolean {
    println("runCheck")
    return true
}

fun compareTwosComplement() {
    listOf(0x80, 1, 0x86, 0xFF, 0xFE)
        .map { it.toByte() }
        .forEach(::printTwosComplement)

    for(i in 0..0xFF) {
        val byte = i.toByte()
        require(byte.toInt() == twosComplement(byte)) {
            println("Automatic and manual Two's complement differ!")
        }
    }
}

fun printTwosComplement(byte: Byte) {
    println("$byte = 0x${"%02x".format(byte)} -> manually ${twosComplement(byte)} automatic: ${byte.toInt()}")
}

fun twosComplement(byte: Byte): Int {
    // convert to signed int from i8:
    return if (byte and 0x80.toByte() > 0) {
        -(0x80 - (byte and 0x7f)) // 2's complement
    } else {
        byte.toInt()
    }
}

fun check1() {
    val bool = true
    val job: Any? = null
    val perm = false

    val c1 = !(bool && job != null && !perm)
    val c2 = !bool || job == null || perm

    println("c1 $c1 c2 $c2")
}

fun checkNulls() {
    tstNullableCheck(Container(true))
    tstNullableCheck(Container(false))
    tstNullableCheck(null)
}

fun tstHex(i: Int) {
    println("$i -> >${formatAsHex(i)}<")
}

private fun formatAsHex(int: Int): String {
    return "%02x".format(int)
}

fun testHexFormat() {
    tstHex(5)
    tstHex(0x55)
    tstHex(0xff)
}


data class Test(val a: Int, val b: Int) {
    val c = a+b

    override fun toString(): String {
        return "$a $b $c"
    }
}

data class Container(val isScanning: Boolean)

fun tstNullableCheck(t: Container?) {
    val c1 = t == null || !t.isScanning
    val c2 = t == null || t.isScanning
    val c3 = !(t?.isScanning == true)
    val c4 = t?.isScanning != true
    val c5 = t?.isScanning == false
    val c6 = t?.isScanning != false

    println("$t: c1 $c1, c2 $c2, c3 $c3, c4 $c4 c5 $c5 c6 $c6")
}

fun testChangeListInPlace() {
    var list = listOf(Test(1,1), Test(2,2), Test(3,3))
    println("$list")
//    list.find{ it.a == 2 }?.b = 6
    list = list.toMutableList().apply {
        val index = indexOfFirst { it.a == 2 }
        this[index] = this[index].copy(b = 6)
    }
    println("$list")
//    list = list.changeFirstInPlace({it.a == 3}) {
//        it.copy(b = it.b+1)
//    }
    println("$list")
}

//fun <T>MutableList<T>.changeFirstInPlace(condition: (T) -> Boolean, change: (T) -> T) = this.apply {
//    try {
//        val index = indexOfFirst(condition)
//        this[index] = change(this[index])
//    } catch (e: IndexOutOfBoundsException) {
//        // Do nothing
//    }
//}

fun flatMapOnMap() {
    val map = mapOf<String, Map<Int, String>>("first" to mapOf(1 to "one", 2 to "two"), "second" to mapOf(3 to "three"))

    println("${map.flatMap { it.value.values }}")
}

fun extendableEnums() {
    val a = OuterEnum.Enum1.a
    val d = OuterEnum.Enum2.d
    val checks = listOf<Boolean>(
        a.equals(d),
        (a as OuterEnum) == (d as OuterEnum),
        (a as OuterEnum).equals(d as OuterEnum),
        (a as OuterEnum).equals(d),
        a.equals(d as OuterEnum)
    )
    println("extendableEnums equals: $checks")
    checks(a,d)
    
    println("b.name: ${OuterEnum.Enum1.bIsAwesome.name}")
}

fun checks(a: OuterEnum, d: OuterEnum) {
    val checks = listOf<Boolean>(
        a.equals(d),
        (a as OuterEnum) == (d as OuterEnum)
    )
    println("extendableEnums equals: $checks")
}

interface OuterEnum {
    enum class Enum1: OuterEnum {
        a,bIsAwesome,c
    }

    enum class Enum2: OuterEnum {
        d,e,f
    }
}

fun checkComparable() {
    val a = IntWrapper(1)
    val b = IntWrapper(2)
    val c = IntWrapper(1)
    println("${b > a}, ${a < b}, ${a == c}")
}

data class IntWrapper(val i: Int): Comparable<IntWrapper> {
    override fun compareTo(other: IntWrapper): Int {
        val ii = other.i
        return when {
            i == ii -> 0
            i > ii -> 1
            i < ii -> -1
            else -> 0
        }
    }
}

fun checkSplit() {
    val input = "2.0.0 beta 7"
    listOf(
        input.split(" "),
        input.split("\\s+"),
        input.split("\\s+".toRegex())
    ).forEach{
        println("$it")
    }
}
