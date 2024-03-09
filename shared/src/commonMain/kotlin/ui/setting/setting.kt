package ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScaleTransition
import cafe.adriel.voyager.transitions.SlideTransition
import dev.icerock.moko.resources.FontResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.library.MR
import org.koin.compose.koinInject

class Setting {

    private val _transitions = MutableStateFlow(PageTransitions.ScaleTransition)
    val transitions = _transitions.asStateFlow()

    private val _theme = MutableStateFlow(ThemeStyle.ThemeOne)
    val theme = _theme.asStateFlow()

    private val _font = MutableStateFlow(ThemeFont.MulishLight)
    val font = _font.asStateFlow()

}


enum class PageTransitions{
    FadeTransition,
    SlideTransition,
    ScaleTransition
}

enum class ThemeStyle{
    ThemeOne,
    ThemeTow,
    ThemeThree
}

enum class ThemeFont(val fontResource: FontResource){
    MulishLight(MR.fonts.Mulish.light),
    MadimiOne(MR.fonts.MadimiOne.regular),
    EBG(MR.fonts.EBGaramond_wght.eBGaramond_wght)
}

@Composable
fun SettingTransitions(navigator: Navigator){
    val transitions = koinInject<Setting>().transitions.collectAsState()
    when(transitions.value){
        PageTransitions.FadeTransition -> FadeTransition(navigator)
        PageTransitions.SlideTransition -> SlideTransition(navigator)
        PageTransitions.ScaleTransition -> ScaleTransition(navigator)
    }
}

fun ThemeStyle.toComposeTheme():ComposeTheme{
    return when(this){
        ThemeStyle.ThemeOne -> ComposeTheme(
            primaryInLightTheme = Color(212, 232, 248),
            onPrimaryInLightTheme = Color(150, 177, 197),
            primaryVariantInLightTheme = Color(79, 106, 128),
            secondaryInLightTheme = Color(231, 249, 253),
            onSecondaryInLightTheme = Color(177, 212, 220),
            secondaryVariantInLightTheme = Color(157, 206, 218),
            surfaceInLightTheme = Color(225, 239, 251),
            onSurfaceInLightTheme = Color(0, 0, 0),
            errorInLightTheme = Color(227, 107, 132),
            onErrorInLightTheme = Color(96, 3, 22),
            backgroundInLightTheme = Color(255, 255, 255),
            onBackgroundInLightTheme = Color(0, 0, 0),
            primaryInDarkTheme = Color(62, 63, 64),
            onPrimaryInDarkTheme = Color(106, 108, 119),
            primaryVariantInDarkTheme = Color(255, 255, 255),
            secondaryInDarkTheme = Color(44, 44, 44),
            onSecondaryInDarkTheme = Color(86, 91, 96),
            secondaryVariantInDarkTheme = Color(255, 255, 255),
            surfaceInDarkTheme = Color(43, 45, 48),
            onSurfaceInDarkTheme = Color(255, 255, 255),
            errorInDarkTheme = Color(255, 0, 51),
            onErrorInDarkTheme = Color(0, 0, 0),
            backgroundInDarkTheme = Color(48, 48, 48),
            onBackgroundInDarkTheme = Color(255, 255, 255)
        )
        ThemeStyle.ThemeTow -> TODO()
        ThemeStyle.ThemeThree -> TODO()
    }
}

class ComposeTheme(
    val primaryInLightTheme   :Color,
    val onPrimaryInLightTheme   :Color,
    val primaryVariantInLightTheme   :Color,
    val secondaryInLightTheme   :Color,
    val onSecondaryInLightTheme   :Color,
    val secondaryVariantInLightTheme   :Color,
    val surfaceInLightTheme  :Color,
    val onSurfaceInLightTheme  :Color,
    val errorInLightTheme  :Color,
    val onErrorInLightTheme  :Color,
    val backgroundInLightTheme  :Color,
    val onBackgroundInLightTheme   :Color,

    val primaryInDarkTheme   :Color,
    val onPrimaryInDarkTheme   :Color,
    val primaryVariantInDarkTheme   :Color,
    val secondaryInDarkTheme   :Color,
    val onSecondaryInDarkTheme   :Color,
    val secondaryVariantInDarkTheme   :Color,
    val surfaceInDarkTheme  :Color,
    val onSurfaceInDarkTheme  :Color,
    val errorInDarkTheme  :Color,
    val onErrorInDarkTheme  :Color,
    val backgroundInDarkTheme  :Color,
    val onBackgroundInDarkTheme   :Color,
)