import io.reactivex.Observable
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KClass

fun main() {
//    testKClass()
//    testTypeParameters()
//    testMethodReturn()
    testAbstractInheritance()
}

interface Inter

data class IA(val a: Int): Inter
data class IB(val b: Int): Inter

data class C(val c: Int)

data class ClassesHolder(val classes: List<KClass<out Inter>>)

fun testKClass() {
    val holder = ClassesHolder(listOf(IA::class, IB::class))

    val objects = listOf(IA(1), IB(2), C(3))
    objects.filter { obj ->
        holder.classes.any { clazz ->
            obj::class == clazz
        }
    }.forEach {
        println("$it")
    }
}

fun testTypeParameters() {
    val mapper = StateMapperImpl as StateMapper<Inter, Inter>
    val clazz = mapper.javaClass
    val kclazz = clazz.kotlin
    println(clazz.simpleName)
    println(kclazz.simpleName)
    println(clazz.classes.toList())
    println(clazz.declaredClasses.toList())
    println(clazz.interfaces.toList())
    println("${kclazz.typeParameters}")
    println("${clazz.typeParameters.toList()}")
    println("${clazz.kotlin.typeParameters}")
    val intrfc = clazz.genericInterfaces.first() as ParameterizedType
    println(intrfc.actualTypeArguments.first())
}


interface StateMapper<I: Inter, S: Inter> {
    fun composeState(input: I): S
    fun updateState(currentState: S): S
//    fun getI() = I::class
}

object StateMapperImpl : StateMapper<IA, IB> {
    override fun updateState(currentState: IB): IB {
        TODO("Not yet implemented")
    }

    override fun composeState(input: IA): IB {
        TODO("Not yet implemented")
    }
}

class ObservableHolder {
    fun someThing(thing: IA): Observable<Int> {
        return Observable.just(1)
    }
}

fun testMethodReturn() {
    val holder = ObservableHolder()
    val method = holder.javaClass.methods.first()
    println(method)
    println(method.returnType)
    println(method.genericReturnType)

    println(IA::class === Inter::class)
    println(IA::class.isInstance(Inter::class))
    println(IA::class.java.interfaces.any { it === Inter::class.java || it.isInstance(Inter::class.java) })

    println(method.parameters.toList())
    println(method.genericParameterTypes.toList())
    println(method.parameterTypes.toList())
    val parameter = method.genericParameterTypes.first()

    println(parameter === Inter::class)
    println(Inter::class.java.isInstance(parameter))
    println(Inter::class.java.interfaces.any { it === Inter::class.java || it.isInstance(Inter::class.java) })
}

abstract class Abs {
    abstract val stuff: String
}

data class AbsImpl(override val stuff: String): Abs()

fun testAbstractInheritance() {
    val parameter = AbsImpl("thing").javaClass as Class<*>
    val required = Abs::class.java
    println(parameter.isInstance(required))
    println(parameter === required)
    println(parameter.superclass.isInstance(required))
    println(required.isInstance(parameter.superclass))
    println(parameter.superclass === required)
    println(parameter.superclass == required)
    println(required.isAssignableFrom(parameter))
}
