import androidVersion.AndroidVersion
import di.configure
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlin.test.Test
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ui.compose.Post.PostDisplayShare.LineChartDataForShow

class GrepTest {
  companion object {
    val sampleData = listOf("123 abc", "abc 123", "123 ABC", "ABC 123")
  }

  @Test
  fun grep() {
    //        val encodedPemContent: String = PEM.encode(
    //            PemContent(
    //                label = PemLabel("KEY"),
    //                bytes = "Hello World".encodeToByteArray()
    //            )
    //        )
    //        println(encodedPemContent)
    val data =
      LineChartDataForShow(
        xData = listOf("1", "2", "3"),
        xAxisTitle = "x",
        yAxisTitle = "sss",
        title = "ss",
        yMap =
          mapOf(
            "sss" to listOf(1f, 2f, 3f),
            "sss2" to listOf(1f, 2f, 3f),
            "sss3" to listOf(1f, 2f, 3f),
          ),
      )
    print(Json.encodeToString(data))
  }

  @Test
  fun shouldFindMatches() {
    val client = HttpClient {
      install(ContentNegotiation) { json() }
      headers {
        append(
          "Authorization",
          "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJlbWFpbCI6InNxdDE4NzUwMDE2MTkzQDE2My5jb20iLCJpZCI6MywiZXhwIjoxNzExOTUxNzM0LCJpc3MiOiJGdVRhbGsifQ.Ce4tDrX_8PRKBX3tp_1XSTSDl1q" +
            "mANj2aOiJsePSGqQ",
        )
      }
      install(DefaultRequest) { url("http://127.0.0.1:9077") }
      install(Logging)
      install(HttpCookies) {}
      install(HttpRedirect) { checkHttpMethod = false }
      configure()
    }
    runBlocking {
      val result = client.get("/static/config/androidVersion.json").bodyAsText()
      println(result)
      val data: AndroidVersion = Json.decodeFromString(result)
      println(data.version.last().canUse)
    }
  }
}
