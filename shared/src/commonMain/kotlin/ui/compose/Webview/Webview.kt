package ui.compose.Webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import org.koin.compose.koinInject
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.network.CollectWithContent
import kotlin.jvm.Transient

/**
 * 自定义的浏览器界面 一级界面
 * @param start String
 * @param parentPaddingControl ParentPaddingControl
 */
@Composable
fun OwnWebViewScreen(
    start: String,
    jwchEnv: Boolean,
    parentPaddingControl: ParentPaddingControl,
) {
    val viewModel: WebviewViewModel = koinInject()
    val clientState = viewModel.clientState.collectAsState()
    LaunchedEffect(Unit) {
        if (jwchEnv) {
            viewModel.getClassScheduleClient()
        }
    }
    var url = start
    if (jwchEnv) {
        clientState.CollectWithContent(
            success = {
                url = start.replace("{id}", it!!)
                val state = rememberWebViewState(url)
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
            },
        )
    } else {
        val state = rememberWebViewState(url)
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
}


class WebViewRouteScreen(
    val url: String
) : Screen {
    @Composable
    override fun Content() {
        Text("this is a test")
    }
}

class WebViewVoyagerScreen(
    val url: String,
    val jwchEnv: Boolean,
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
    @Composable
    override fun Content() {
        OwnWebViewScreen(
            url,
            jwchEnv,
            parentPaddingControl = parentPaddingControl
        )
    }
}