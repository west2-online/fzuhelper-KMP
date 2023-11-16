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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asComposeImageBitmap
import androidx.compose.ui.interop.LocalUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.posix.memcpy


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

//ios main


actual class ImagePicker(
    private val rootController: UIViewController
) {
    private val imagePickerController = UIImagePickerController().apply {
        sourceType = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
    }

    private var onImagePicked: (ByteArray) -> Unit = {}

    @OptIn(ExperimentalForeignApi::class)
    private val delegate = object : NSObject(), UIImagePickerControllerDelegateProtocol,
        UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingImage: UIImage,
            editingInfo: Map<Any?, *>?
        ) {
            val imageNsData = UIImageJPEGRepresentation(didFinishPickingImage, 1.0)
                ?: return
            val bytes = ByteArray(imageNsData.length.toInt())
            memcpy(bytes.refTo(0), imageNsData.bytes, imageNsData.length)

            onImagePicked(bytes)

            picker.dismissViewControllerAnimated(true, null)
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            picker.dismissViewControllerAnimated(true, null)
        }
    }

    @Composable
    actual fun registerPicker(onImagePicked: (ByteArray) -> Unit) {
        this.onImagePicked = onImagePicked
    }

    actual fun pickImage() {
        rootController.presentViewController(imagePickerController, true) {
            imagePickerController.delegate = delegate
        }
    }
}

actual class PlatformContext (val iosController: ProvidableCompositionLocal<UIViewController>)

@Composable
actual fun getPlatformContext(): PlatformContext = util.PlatformContext(LocalUIViewController)


actual class ImagePickerFactory actual constructor(private val context: PlatformContext) {

    @Composable
    actual fun createPicker(): ImagePicker {
        val rootController = context.iosController.current
        return remember {
            ImagePicker(rootController)
        }
    }
}

@Composable
actual fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap? {
    return remember(bytes) {
        if (bytes != null) {
            Bitmap.makeFromImage(
                Image.makeFromEncoded(bytes)
            ).asComposeImageBitmap()
        } else {
            null
        }
    }
}