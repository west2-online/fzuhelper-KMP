package ui.compose.Webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import kotlin.jvm.Transient

//自定义浏览器
@Composable
fun OwnWebViewScreen(
    start: String,
    parentPaddingControl: ParentPaddingControl,
){
    val state = rememberWebViewState(start)
    val webNavigation = rememberWebViewNavigator()
    state.webSettings.apply {
        isJavaScriptEnabled = true
        androidWebSettings.apply {
            isAlgorithmicDarkeningAllowed = true
            safeBrowsingEnabled = false
            this.allowFileAccess = true
        }
    }
    WebView(
        state,
        modifier = Modifier
            .fillMaxSize()
            .parentSystemControl(parentPaddingControl),
        navigator = webNavigation
    )
}


class WebViewRouteScreen(
    val url:String
): Screen {
    @Composable
    override fun Content() {
        Text("this is a test")
    }
}

class WebViewVoyagerScreen(
    val url : String,
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
):Screen{
    @Composable
    override fun Content() {
        OwnWebViewScreen(
            url,
            parentPaddingControl = parentPaddingControl
        )
    }
}