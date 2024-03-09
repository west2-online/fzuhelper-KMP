package ui.compose.Authentication

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.setting.SettingTransitions


object LoginAndRegisterVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        Navigator(LoginVoyagerScreen()){ navigator ->
            SettingTransitions(navigator)
        }
    }
}

