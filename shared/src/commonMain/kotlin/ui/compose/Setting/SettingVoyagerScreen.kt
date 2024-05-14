package ui.compose.Setting

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import util.compose.ParentPaddingControl
import util.compose.SettingTransitions
import util.compose.defaultSelfPaddingControl
import kotlin.jvm.Transient


class SettingVoyagerScreen(
    @Transient
    private val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
    @Composable
    override fun Content() {
        Box{
            Navigator(MainSettingVoyagerScreen(parentPaddingControl)){
                SettingTransitions(it)
            }
        }
    }
}