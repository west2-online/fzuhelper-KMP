package ui.compose.Webview

import BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState

@Composable
fun OwnWebViewScreen(
    start : String,
    back:()->Unit
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
    BackHandler(!webNavigation.canGoBack){
        back.invoke()
    }
    WebView(
        state,
        modifier = Modifier
            .fillMaxSize(),
        navigator = webNavigation
    )
}