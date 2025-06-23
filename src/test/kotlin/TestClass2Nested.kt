import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TestClass2 {
    @Test
    fun test21() = runBlocking { delay(1000) }

    @Test
    fun test22() = runBlocking { delay(1000) }

    @Test
    fun test23() = runBlocking { delay(1000) }

    @Test
    fun test24() = runBlocking { delay(1000) }

    @Test
    fun test25() = runBlocking { delay(1000) }

    @Test
    fun test26() = runBlocking { delay(1000) }

    @Test
    fun test27() = runBlocking { delay(1000) }

    @Test
    fun test28() = runBlocking { delay(1000) }

    @Test
    fun test29() = runBlocking { delay(1000) }

    @Nested
    class TestClass3_Nested {
        @Test
        fun test31() = runBlocking { delay(1000) }

        @Test
        fun test32() = runBlocking { delay(1000) }

        @Test
        fun test33() = runBlocking { delay(1000) }

        @Test
        fun test34() = runBlocking { delay(1000) }

        @Test
        fun test35() = runBlocking { delay(1000) }

        @Test
        fun test36() = runBlocking { delay(1000) }

        @Test
        fun test37() = runBlocking { delay(1000) }

        @Test
        fun test38() = runBlocking { delay(1000) }

        @Test
        fun test39() = runBlocking { delay(1000) }
    }
}