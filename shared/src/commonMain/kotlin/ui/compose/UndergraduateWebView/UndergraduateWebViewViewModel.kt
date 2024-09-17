package ui.compose.UndergraduateWebView

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewState
import config.JWCH_BASE_URL
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import util.compose.ErrorText
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.network.CollectWithContentInBox
import kotlin.jvm.Transient

class UndergraduateWebViewVoyagerScreen(
  val url: String,
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
) : Screen {
  @Composable
  override fun Content() {
    OwnWebViewScreen(url, parentPaddingControl = parentPaddingControl)
  }
}

/**
 * 自定义的浏览器界面 一级界面
 *
 * @param start String
 * @param parentPaddingControl ParentPaddingControl
 */
@Composable
fun OwnWebViewScreen(start: String, parentPaddingControl: ParentPaddingControl) {
  val viewModel: UndergraduateWebViewViewModel = koinInject()
  val clientState = viewModel.clientState.collectAsState()
  val scope = rememberCoroutineScope()
  LaunchedEffect(Unit) { viewModel.getJwchClient() }
  var url = start
  val isLoadingCookie = remember { mutableStateOf(true) }
  clientState.CollectWithContentInBox(
    success = {
      url = start.replace("{id}", it.id)
      val state = rememberWebViewState(url)
      LaunchedEffect(state) {
        snapshotFlow { state.loadingState }
          .filter { it is LoadingState.Initializing }
          .collect { loadingState ->
            it.cookie.forEach { cookieItem ->
              state.cookieManager.setCookie(JWCH_BASE_URL, cookieItem)
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
          allowFileAccess = true
          useWideViewPort = true
          domStorageEnabled = true
          textZoom = 80
        }
      }
      if (!isLoadingCookie.value) {
        WebView(state, modifier = Modifier.fillMaxSize(), navigator = webNavigation)
      } else {
        CircularProgressIndicator()
      }
    },
    loading = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
    error = {
      ErrorText(
        text = "加载失败",
        onClick = { scope.launch { viewModel.getJwchClient() } },
        boxModifier = Modifier.fillMaxSize(),
      )
    },
    modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl),
  )
}
