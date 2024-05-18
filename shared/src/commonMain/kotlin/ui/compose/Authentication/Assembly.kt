package ui.compose.Authentication

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import util.compose.SettingTransitions

/**
 * Login and register voyager screen
 * 登录界面的一级界面 包括登录界面和注册界面 用于切换
 * @constructor Create empty Login and register voyager screen
 */
object LoginAndRegisterVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        Navigator(LoginVoyagerScreen()){ navigator ->
            SettingTransitions(navigator)
        }
    }
}

