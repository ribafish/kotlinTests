
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.test.assertEquals
import org.junit.jupiter.api.Test
import java.net.ConnectException
import kotlin.test.assertFailsWith


class Test {

    @Test
    @Throws(IOException::class)
    fun whenGetRequest_thenCorrect() {
        val client = OkHttpClient()
        val request: Request = Request.Builder()
            .url(BASE_URL)
            .build()

        val call: Call = client.newCall(request)

        // Should fail as we're set up a proxy using systemProperties for the `Test` tasks.
        assertFailsWith(ConnectException::class) {
            val response: Response = call.execute()
            assertEquals(200, response.code)
        }
    }

    companion object {
        private const val BASE_URL = "https://raw.github.com/square/okhttp/master/README.md"
    }
}