import androidx.compose.runtime.Composable

actual fun getPlatformName(): String = "Android"

@Composable fun MainView() = App()

@Composable
actual fun BackHandler(isEnabled: Boolean, onBack: () -> Unit) {
    androidx.activity.compose.BackHandler(isEnabled, onBack)
}

