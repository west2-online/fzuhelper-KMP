package ui.compose.Webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState

//自定义浏览器
@Composable
fun OwnWebViewScreen(
    start : String,
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
            .fillMaxSize(),
        navigator = webNavigation
    )
}

class WebViewRouteNode(
    buildContext: BuildContext,
    val url:String
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        OwnWebViewScreen(
            url
        )
    }
}