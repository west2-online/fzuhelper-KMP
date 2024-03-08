package ui.compose.Authentication

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.bumble.appyx.utils.multiplatform.Parcelable
import dev.icerock.moko.parcelize.Parcelize


object LoginAndRegisterVoyagerScreen : Screen {
    @Composable
    override fun Content() {
        Navigator(LoginVoyagerScreen())
    }
}

