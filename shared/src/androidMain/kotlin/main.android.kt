
import android.graphics.BitmapFactory
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.okhttp.OkHttpConfig
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = App()

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(isEnabled, onBack)
}

actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
): KoinDefinition<T> = viewModel(qualifier = qualifier, definition = definition)


actual fun ByteArray.asImageBitmap(): ImageBitmap{
    val imageBitmap = ImageBitmap(1, 1)
    val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)
    return bitmap.asImageBitmap()
}

actual fun HttpClientConfig<*>.configureForPlatform() {
    engine {
        this as OkHttpConfig
        config {
//            val trustAllCert = AllCertsTrustManager()
//            val sslContext = SSLContext.getInstance("SSL")
//            sslContext.init(null, arrayOf(trustAllCert), SecureRandom())
//            sslSocketFactory(sslContext.socketFactory, trustAllCert)
            sslSocketFactory(getSSLSocketFactory, trustAllCerts[0])
            connectTimeout(5, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)
        }
    }
}

val trustAllCerts = arrayOf<X509TrustManager>(object : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate?>?, authType: String?) {
    }

    override fun checkServerTrusted(chain: Array<X509Certificate?>?, authType: String?) {}

    override fun getAcceptedIssuers(): Array<X509Certificate?> {
        return arrayOfNulls(0)
    }
})

val getSSLSocketFactory: SSLSocketFactory by lazy {
    val sslContext = SSLContext.getInstance("TLS")
    sslContext.init(
        null, trustAllCerts,
        SecureRandom()
    )
    sslContext.socketFactory
}