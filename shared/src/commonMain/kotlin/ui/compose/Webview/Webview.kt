package ui.compose.Webview

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import kotlin.jvm.Transient
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl

class GeneralWebViewVoyagerScreen(
  val url: String,
  val header: Map<String, String> = mapOf(),
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl(),
) : Screen {
  @Composable
  override fun Content() {
    val state = rememberWebViewState(url, header)
    WebView(
      state = state,
      modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl),
    )
  }
}
