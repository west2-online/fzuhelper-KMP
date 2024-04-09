package ui.compose.Test

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import asImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import configureForPlatform
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.cookies.cookies
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.Cookie
import io.ktor.http.Url

class TestVoyagerScreen :Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        //192.168.31.1
        val data = remember {
            mutableStateOf<ByteArray?>(null)
        }
        val cookie = remember {
            mutableStateOf<Cookie?>(null)
        }
        LaunchedEffect(Unit){
            try {
                val client = HttpClient(){
                    install(HttpCookies){
                        storage = CustomCookiesStorage(cookie)
                    }
                    configureForPlatform()
                }
                val image = client.get("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp").readBytes()
                data.value = image
                println("++++++++++++++++++++++${ client.cookies("/") }")
            }catch (e:Exception){
                println(e.message)
            }

        }
        Column {
            data.value?.let{
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .statusBarsPadding()
                        .size(width = 50.dp, height = 20.dp)
                )
            }
            cookie.value?.let {
                Text(it.toString())
            }
        }
    }
}

public class CustomCookiesStorage(
    val cookieForSave: MutableState<Cookie?>
) : CookiesStorage {
    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        cookieForSave.value = cookie
        println(cookie)
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        return listOf()
    }

    override fun close() {

    }
}