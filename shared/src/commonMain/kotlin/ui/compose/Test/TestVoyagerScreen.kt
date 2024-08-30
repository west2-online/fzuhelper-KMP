package ui.compose.Test

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import configureForPlatform
import dev.whyoleg.cryptography.serialization.pem.PEM
import dev.whyoleg.cryptography.serialization.pem.PemContent
import dev.whyoleg.cryptography.serialization.pem.PemLabel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.Cookie
import io.ktor.http.Url

class TestVoyagerScreen : Screen {
  @OptIn(ExperimentalMaterial3Api::class, ExperimentalUnsignedTypes::class)
  @Composable
  override fun Content() {
    // 192.168.31.1
    val data = remember { mutableStateOf<ByteArray?>(null) }
    val cookie = remember { mutableStateOf<Cookie?>(null) }
    LaunchedEffect(Unit) {
      //            val message = ("Ladies and Gentlemen of the class of '99: If I could offer you "
      // +
      //                    "only one tip for the future, sunscreen would be
      // it.").encodeToUByteArray()
      //
      //            val key = "futalk".encodeToUByteArray()
      //
      //            val nonce = LibsodiumRandom.buf(24)
      //
      //            val encrypted = SecretBox.easy(message, nonce, key)
      //            val decrypted = SecretBox.openEasy(encrypted, nonce, key)
      //            println("encrypted==============="+encrypted)
      val encodedPemContent: String =
        PEM.encode(PemContent(label = PemLabel("KEY"), bytes = "Hello World".encodeToByteArray()))
      println(encodedPemContent)
      try {
        val client =
          HttpClient() {
            install(HttpCookies) { storage = CustomCookiesStorage(cookie) }
            configureForPlatform()
          }
        val image = client.get("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp").readBytes()
        data.value = image
        println("++++++++++++++++++++++${ client.cookies("/") }")
      } catch (e: Exception) {
        println(e.message)
      }
    }
    //
    //        Column {
    //            data.value?.let{
    //                Image(
    //                    bitmap = it.asImageBitmap(),
    //                    contentDescription = null,
    //                    modifier = Modifier
    //                        .statusBarsPadding()
    //                        .size(width = 50.dp, height = 20.dp)
    //                )
    //            }
    //            cookie.value?.let {
    //                Text(it.toString())
    //            }
    //        }
    //        XYChart(
    //            false,"This is a test", xAxisTitle = "x", data = LineChartData(
    //                listOf("1","2","3","4","5","6","7","8","9","10","11","12"),
    //                mapOf(
    //                    "test1" to expandedList,
    //                    "test2" to expandedList,
    //                    "test3" to expandedList
    //                )
    //            )
    //        )
  }
}

val expandedList =
  listOf(
    3.0.toFloat(),
    4.0.toFloat(),
    5.0.toFloat(),
    3.0.toFloat(),
    4.0.toFloat(),
    5.0.toFloat(),
    3.0.toFloat(),
    4.0.toFloat(),
    5.0.toFloat(),
    3.0.toFloat(),
    4.0.toFloat(),
    5.0.toFloat(),
  )

public class CustomCookiesStorage(val cookieForSave: MutableState<Cookie?>) : CookiesStorage {
  override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
    cookieForSave.value = cookie
    println(cookie)
  }

  override suspend fun get(requestUrl: Url): List<Cookie> {
    return listOf()
  }

  override fun close() {}
}
