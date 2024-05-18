package ui.compose.Feedback

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import util.compose.ParentPaddingControl
import util.compose.SettingTransitions
import util.compose.defaultSelfPaddingControl


/**
 * 反馈界面 一级界面
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class FeedbackVoyagerScreen(
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
):Screen{
    @Composable
    override fun Content() {
        Navigator(FeedbackListVoyagerScreen(
            parentPaddingControl
        )){ navigator ->
            SettingTransitions(navigator = navigator)
        }
    }
}