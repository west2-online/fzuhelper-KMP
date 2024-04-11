package ui.compose.Massage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import ui.setting.SettingTransitions
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import kotlin.jvm.Transient


class MassageVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
): Tab {
    override val options: TabOptions
        @Composable
        get(){
            return remember {
                TabOptions(
                    index = 0u,
                    title = ""
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(
            MassageVoyagerList(
                parentPaddingControl = parentPaddingControl
            )
        ){ navigator ->
            SettingTransitions(navigator)
        }
    }
}