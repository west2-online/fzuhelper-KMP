package ui.compose.ModifierInformation

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import data.person.UserData.Data
import util.compose.SettingTransitions
import kotlin.jvm.Transient

/**
 * 修改用户信息总页面
 * @property userData Data
 * @constructor
 */
class ModifierInformationVoyagerScreen(
    @Transient
    val userData: Data,
):Screen{
    @Composable
    override fun Content() {
        Navigator(ModifierInformationItemsVoyagerScreen(userData)){
            SettingTransitions(it)
        }
    }
}


