import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ui.route.RouteHost
import ui.util.FuTalkTheme



@Composable
fun App() {
    FuTalkTheme {
        RouteHost(
            modifier = Modifier.fillMaxSize()
        )
    }
}


expect fun getPlatformName(): String

@Composable
expect fun BackHandler(isEnabled: Boolean, onBack: ()-> Unit)