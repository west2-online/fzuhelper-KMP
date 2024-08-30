package ui.compose.Setting

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import kotlin.jvm.Transient
import util.compose.ParentPaddingControl
import util.compose.SettingTransitions
import util.compose.defaultSelfPaddingControl

/**
 * 设置主界面 一级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class SettingVoyagerScreen(
  @Transient private val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    Box { Navigator(MainSettingVoyagerScreen(parentPaddingControl)) { SettingTransitions(it) } }
  }
}
