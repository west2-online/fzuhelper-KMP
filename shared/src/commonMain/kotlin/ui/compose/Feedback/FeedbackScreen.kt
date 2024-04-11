package ui.compose.Feedback

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.setting.SettingTransitions
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl


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