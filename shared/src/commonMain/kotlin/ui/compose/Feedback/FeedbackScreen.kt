package ui.compose.Feedback

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.setting.SettingTransitions


class FeedbackVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        Navigator(FeedbackListVoyagerScreen()){ navigator ->
            SettingTransitions(navigator = navigator)
        }
    }
}