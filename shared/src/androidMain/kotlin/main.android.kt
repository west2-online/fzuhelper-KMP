// Android main
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.futalk.kmm.FuTalkDatabase
import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import io.ktor.utils.io.core.toByteArray
import okhttp3.Protocol
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

actual fun getPlatformName(): String = "Android"

@Composable
actual fun Modifier.ComposeSetting(): Modifier {
  return this.safeContentPadding()
}

@Composable
actual fun BackHandlerWithPlatform(isEnabled: Boolean, onBack: () -> Unit) {
  androidx.activity.compose.BackHandler(isEnabled, onBack)
}

actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
  qualifier: Qualifier?,
  noinline definition: Definition<T>,
): KoinDefinition<T> = viewModel(qualifier = qualifier, definition = definition)

actual fun ByteArray.asImageBitmap(): ImageBitmap {
  val imageBitmap = ImageBitmap(1, 1)
  val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
  return bitmap.asImageBitmap()
}

actual fun HttpClientConfig<*>.configureForPlatform() {
  engine {
    this as OkHttpConfig
    config {
      protocols(listOf(Protocol.HTTP_1_1))
      //            val trustAllCert = AllCertsTrustManager()
      //            val sslContext = SSLContext.getInstance("SSL")
      //            sslContext.init(null, arrayOf(trustAllCert), SecureRandom())
      //            sslSocketFactory(sslContext.socketFactory, trustAllCert)
      sslSocketFactory(getSSLSocketFactory, trustAllCerts[0])
      connectTimeout(20, TimeUnit.SECONDS)
      readTimeout(20, TimeUnit.SECONDS)
    }
  }
}

val trustAllCerts =
  arrayOf<X509TrustManager>(
    object : X509TrustManager {
      override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

      override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

      override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
      }
    },
  )

val getSSLSocketFactory: SSLSocketFactory by lazy {
  val sslContext = SSLContext.getInstance("TLS")
  sslContext.init(null, trustAllCerts, SecureRandom())
  sslContext.socketFactory
}

actual fun initStore(): KVault {
  return KVault(context = MyApplication.getContext())
}

actual class PlatformContext(val androidContext: Context)

@Composable
actual fun getPlatformContext(): PlatformContext = PlatformContext(LocalContext.current)

actual class ImagePicker(private val activity: ComponentActivity) {
  private lateinit var getContent: ActivityResultLauncher<String>

  @Composable
  actual fun registerPicker(onImagePicked: (ByteArray) -> Unit) {
    getContent =
      rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
          activity.contentResolver.openInputStream(uri)?.use { onImagePicked(it.readBytes()) }
        }
      }
  }

  actual fun pickImage() {
    getContent.launch("image/*")
  }
}

actual class ImagePickerFactory actual constructor(context: PlatformContext) {

  @Composable
  actual fun createPicker(): ImagePicker {
    val activity = LocalContext.current as ComponentActivity
    return remember(activity) { ImagePicker(activity) }
  }
}

@Composable
actual fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
  return remember(bytes) {
    if (bytes != null) {
      BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    } else {
      null
    }
  }
}

actual fun createDriver(): SqlDriver {
  return AndroidSqliteDriver(FuTalkDatabase.Schema, MyApplication.instance, "futalk.db")
}

actual fun getVersionFileName(): String {
  return "androidVersion.json"
}

actual fun HttpClientEngineConfig.ktorConfig() {}


actual fun getStringMd5_32(string: String): String {
  val md5: MessageDigest
  var encodeStr = ""
  try {
    md5 = MessageDigest.getInstance("MD5")
    encodeStr = byte2Hex(md5.digest(string.toByteArray()))
  } catch (e: NoSuchAlgorithmException) {
    e.printStackTrace()
  }
  return encodeStr
}

/**
 * 将byte转为16进制
 *
 * @param bytes
 * @return
 */
fun byte2Hex(bytes: ByteArray): String {
  val stringBuffer = StringBuilder()
  var temp: String
  for (aByte in bytes) {
    temp = Integer.toHexString(aByte.toInt() and 0xFF)
    if (temp.length == 1) {
      //1得到一位的进行补0操作
      stringBuffer.append("0")
    }
    stringBuffer.append(temp)
  }
  return stringBuffer.toString()
}


actual fun debug(string: String) {
  Log.d("DEBUG", string)
}
