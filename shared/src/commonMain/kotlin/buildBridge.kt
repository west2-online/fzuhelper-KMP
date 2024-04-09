
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import app.cash.sqldelight.db.SqlDriver
import com.futalk.kmm.FuTalkDatabase
import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.HttpClientEngineConfig
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier

expect fun getPlatformName(): String

@Composable
fun BackHandler(isEnabled: Boolean, onBack: ()-> Unit){
    BackHandlerWithPlatform(isEnabled,onBack)
}

@Composable
expect fun BackHandlerWithPlatform(isEnabled: Boolean, onBack: ()-> Unit)

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>

expect fun ByteArray.asImageBitmap(): ImageBitmap

expect fun HttpClientConfig<*>.configureForPlatform()

expect fun initStore(): KVault

@Composable
expect fun Modifier.ComposeSetting():Modifier

expect class ImagePickerFactory(context: PlatformContext) {
    @Composable
    fun createPicker(): ImagePicker
}

expect class ImagePicker {
    @Composable
    fun registerPicker(onImagePicked: (ByteArray) -> Unit)
    fun pickImage()
}

@Composable
expect fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap?

expect class PlatformContext

@Composable
expect fun getPlatformContext(): PlatformContext


expect fun createDriver(): SqlDriver

fun createDatabase(): FuTalkDatabase {
    val driver =  createDriver()
    return FuTalkDatabase(driver)
}

expect fun getVersionFileName():String

expect fun HttpClientEngineConfig.ktorConfig()