import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.window.ComposeUIViewController
import androidx.datastore.core.DataStore
import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.darwin.DarwinClientEngineConfig
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.skia.Image
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersHolder
import org.koin.core.qualifier.Qualifier
import org.koin.core.scope.Scope

actual fun getPlatformName(): String = "iOS"

fun MainViewController() = ComposeUIViewController { App() }

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    LaunchedEffect(isEnabled) {
//        store.events.collect {
//            if(isEnabled) {
//                onBack()
//            }
//        }
    }
}

//iosMain
actual inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier?,
    noinline definition: Definition<T>,
): KoinDefinition<T> = factory(qualifier = qualifier, definition = definition)

actual fun ByteArray.asImageBitmap(): ImageBitmap{
    return Image.makeFromEncoded(this).toComposeImageBitmap()
}

actual fun HttpClientConfig<*>.configureForPlatform() {
    engine {
        this as DarwinClientEngineConfig
        // TODO: Add iOS config
        TODO()
    }
}

actual fun initStore(): KVault{
    return KVault()
}