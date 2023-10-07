import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.window.ComposeUIViewController

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