package ui.compose.Manage

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlin.jvm.Transient
import org.koin.compose.koinInject
import util.compose.ParentPaddingControl
import util.compose.SettingTransitions
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl

/**
 * 管理界面 一级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class ManageVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    val viewModel = koinInject<ManageViewModel>()
    Navigator(ManagePostVoyagerScreen) { navigator ->
      Column(modifier = Modifier.parentSystemControl(parentPaddingControl)) {
        Row(
          modifier =
            Modifier.background(MaterialTheme.colors.primarySurface)
              .fillMaxWidth()
              .wrapContentHeight()
              .statusBarsPadding()
        ) {
          Row(
            modifier = Modifier.weight(1f).height(56.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            val lazyPagingItemsForPost =
              koinInject<ManageViewModel>().postReportPageList.collectAsLazyPagingItems()
            val lazyPagingItemsForComment =
              koinInject<ManageViewModel>().commentReportPageList.collectAsLazyPagingItems()
            IconButton(
              onClick = {
                lazyPagingItemsForComment.refresh()
                lazyPagingItemsForPost.refresh()
                viewModel.refresh()
              }
            ) {
              Icon(Icons.Default.Refresh, contentDescription = "Localized description")
            }
          }
          Box(modifier = Modifier.height(56.dp).aspectRatio(1f)) {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
              Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              DropdownMenuItem(
                onClick = {
                  if (navigator.lastItem !is ManagePostVoyagerScreen) {
                    navigator.replaceAll(ManagePostVoyagerScreen)
                  }
                  expanded = false
                }
              ) {
                Text("管理帖子")
              }
              DropdownMenuItem(
                onClick = {
                  if (navigator.lastItem !is ManageCommentVoyagerScreen) {
                    navigator.replaceAll(ManageCommentVoyagerScreen)
                  }
                  expanded = false
                }
              ) {
                Text("管理评论")
              }
              DropdownMenuItem(
                onClick = {
                  if (navigator.lastItem !is ManageOpenImageVoyagerScreen) {
                    navigator.replaceAll(ManageOpenImageVoyagerScreen)
                  }
                  expanded = false
                }
              ) {
                Text("管理开屏页")
              }
              DropdownMenuItem(
                onClick = {
                  if (navigator.lastItem !is MangeRibbonVoyagerScreen) {
                    navigator.replaceAll(MangeRibbonVoyagerScreen())
                  }
                  expanded = false
                }
              ) {
                Text("管理轮播页")
              }
              DropdownMenuItem(
                onClick = {
                  if (navigator.lastItem !is ManageAdministratorVoyager) {
                    navigator.replaceAll(ManageAdministratorVoyager)
                  }
                  expanded = false
                }
              ) {
                Text("管理管理员")
              }
            }
          }
        }
        SettingTransitions(navigator)
      }
    }
  }
}
