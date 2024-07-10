package ui.compose.Webview

import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.cookie.Cookie
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.WebViewState
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import config.JWCH_BASE_URL
import kotlinx.coroutines.flow.filter
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
    val isLoadingCookie = remember {
        mutableStateOf(true)
    }
    if (jwchEnv) {
        clientState.CollectWithContent(
            success = {
                url = start.replace("{id}", it.id)
                val state = rememberWebViewState(url)
                LaunchedEffect(state) {
                    snapshotFlow { state.loadingState }
                        .filter { it is LoadingState.Initializing }
                        .collect { loadingState ->
                        it.cookie.forEach { cookieItem ->
                            state.cookieManager.setCookie(JWCH_BASE_URL,cookieItem)
                        }
                        isLoadingCookie.value = false
                    }
                }
                val webNavigation = rememberWebViewNavigator()
                state.webSettings.apply {
                    isJavaScriptEnabled = true
                    androidWebSettings.apply {
                        isAlgorithmicDarkeningAllowed = true
                        safeBrowsingEnabled = false
                        this.allowFileAccess = true
                    }
                }
                if (!isLoadingCookie.value){
                    WebView(
                        state,
                        modifier = Modifier
                            .fillMaxSize()
                            .parentSystemControl(parentPaddingControl),
                        navigator = webNavigation
                    )
                }else{
                    CircularProgressIndicator()
                }
            },
            loading = {
                CircularProgressIndicator()
            }
        )
    } else {
        val state = rememberWebViewState(url,mapOf("User-Agent" to "Mozilla/5.0 (Linux; Android 9; ELE-AL00 Build/HUAWEIELE-AL0001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/63.0.3239.83 Mobile Safari/537.36 T7/11.15 baiduboxapp/11.15.5.10 (Baidu; P1 9)"))
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