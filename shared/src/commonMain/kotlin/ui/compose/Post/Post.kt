package ui.compose.Post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlin.jvm.Transient
import org.koin.compose.koinInject
import ui.compose.Main.MainItems
import ui.compose.Report.ReportType
import ui.root.RootAction
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.SettingTransitions
import util.compose.defaultSelfPaddingControl
import util.compose.rememberToastState

/**
 * 帖子界面 一级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @property options TabOptions
 * @constructor
 */
class PostVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Tab {
  override val options: TabOptions
    @Composable
    get() {
      val title = MainItems.POST.tag
      val icon = rememberVectorPainter(MainItems.POST.selectImageVector)
      return remember { TabOptions(index = 0u, title = title, icon = icon) }
    }

  @Composable
  override fun Content() {
    val toastState = rememberToastState()
    val rootAction = koinInject<RootAction>()
    Box(modifier = Modifier.fillMaxSize()) {
      Navigator(
        PostListVoyagerScreen(
          navigateToRelease = { rootAction.navigateFormAnywhereToRelease(listOf()) },
          navigateToReport = {
            rootAction.navigateFormPostToReport(
              ReportType.PostReportType(id = it.Post.Id.toString(), it.Post)
            )
          },
          parentPaddingControl = parentPaddingControl,
        )
      ) { navigator ->
        SettingTransitions(navigator)
      }
      EasyToast(toastState)
    }
  }
}
