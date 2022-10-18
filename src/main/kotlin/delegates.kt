

interface First {
    fun something()
}

interface Second: First {
    fun something2()
}

interface Third: First, Second {
    fun something3()
}

open class Class1: First by FirstDelegate

open class Class2: Class1(), Second by SecondDelegate

open class Class3: Class2(), Third by ThirdDelegate

object FirstDelegate: First {
    override fun something() {
        println("${this.javaClass.simpleName}.something()")
    }
}

object SecondDelegate: Second, First by FirstDelegate {
    override fun something2() {
        println("${this.javaClass.simpleName}.something2()")
    }
}

object ThirdDelegate: Third, Second by SecondDelegate {
    override fun something3() {
        println("${this.javaClass.simpleName}.something3()")
    }
}

class Class3Child(private val parent: Class3Parent): Third by parent, First

class Class3Parent: Third by ThirdDelegate, First

fun main() {
    val c1 = Class1()
    val c2 = Class2()
    val c3 = Class3()
    val c3p = Class3Parent()
    val c3c = Class3Child(c3p)

    println("----- Class1 ------")
    c1.something()

    println("\n----- Class2 ------")
    c2.something()
    c2.something2()

    println("\n----- Class3 ------")
    c3.something()
    c3.something2()
    c3.something3()

    println("\n----- Class3Child ------")
    c3c.something()
    c3c.something2()
    c3c.something3()
}
