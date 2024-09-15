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

/**
 * 注册返回逻辑
 *
 * @param isEnabled Boolean 是否可用
 * @param onBack Function0<Unit>
 */
@Composable
fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
  BackHandlerWithPlatform(isEnabled, onBack)
}

@Composable expect fun BackHandlerWithPlatform(isEnabled: Boolean, onBack: () -> Unit)

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
  qualifier: Qualifier? = null,
  noinline definition: Definition<T>,
): KoinDefinition<T>

/**
 * 将字节数组转为图片二进制
 *
 * @return ImageBitmap
 * @receiver ByteArray
 */
expect fun ByteArray.asImageBitmap(): ImageBitmap

/**
 * 客户端的设置
 *
 * @receiver HttpClientConfig<*>
 */
expect fun HttpClientConfig<*>.configureForPlatform()

/**
 * 获取底层键值对操作对象
 *
 * @return KVault
 */
expect fun initStore(): KVault

@Composable expect fun Modifier.ComposeSetting(): Modifier

/**
 * 获取可用的设备选择图片对象
 *
 * @constructor
 */
expect class ImagePickerFactory(context: PlatformContext) {
  @Composable fun createPicker(): ImagePicker
}

/**
 * 选择设备的图片
 *
 * @constructor
 */
expect class ImagePicker {
  @Composable fun registerPicker(onImagePicked: (ByteArray) -> Unit)

  fun pickImage()
}

@Composable expect fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap?

expect class PlatformContext

@Composable expect fun getPlatformContext(): PlatformContext

/**
 * 获取特定平台的sql驱动
 *
 * @return SqlDriver
 */
expect fun createDriver(): SqlDriver

/**
 * 获取底层数据库操作对象
 *
 * @return FuTalkDatabase
 */
fun createDatabase(): FuTalkDatabase {
  val driver = createDriver()
  return FuTalkDatabase(driver)
}

/**
 * 获取版本文件名
 *
 * @return String
 */
expect fun getVersionFileName(): String

expect fun HttpClientEngineConfig.ktorConfig()

expect fun getStringMd5_32(string: String): String

expect fun debug(string: String)
