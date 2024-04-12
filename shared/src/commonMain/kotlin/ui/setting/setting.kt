package ui.setting

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScaleTransition
import cafe.adriel.voyager.transitions.SlideTransition
import com.liftric.kvault.KVault
import dev.icerock.moko.resources.FontResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.library.MR
import org.koin.compose.koinInject
import util.flow.launchInDefault

const val FontToken = "Font"
const val ThemeToken = "Theme"
const val TransitionToken = "Transition"


class Setting(
    private val kVault: KVault,
    private val _transitions: MutableStateFlow<PageTransitions> = MutableStateFlow(PageTransitions.ScaleTransition),
    private val _theme: MutableStateFlow<ThemeStyle> = MutableStateFlow(ThemeStyle.ThemeOne),
    private val _font: MutableStateFlow<Font> = MutableStateFlow(Font.MulishLight)
) {
    val scope = CoroutineScope(Job())
    init {
        scope.launchInDefault {
            initFont()
        }
        scope.launchInDefault {
            initTheme()
        }
        scope.launchInDefault {
            initTransition()
        }
    }




    val transitions = _transitions.asStateFlow()
    val theme = _theme.asStateFlow()
    val font = _font.asStateFlow()

    fun changeTransitions(pageTransitions : PageTransitions){
        scope.launchInDefault {
            saveTransitionToKValue(pageTransitions)
        }
        scope.launchInDefault {
            _transitions.value = pageTransitions
        }
    }

    fun changeTheme(theme : ThemeStyle){
        scope.launchInDefault {
            saveThemeToKValue(theme)
        }
        scope.launchInDefault {
            _theme.value = theme
        }
    }

    fun changeFont(font : Font){
        scope.launchInDefault {
            saveFontToKValue(font)
        }
        scope.launchInDefault {
            _font.value = font
        }
    }

    private fun saveThemeToKValue(theme : ThemeStyle){
        kVault.set(ThemeToken,theme.serializable)
//        println("save Theme : ${ok}")
    }

    private fun saveFontToKValue(font : Font){
        kVault.set(FontToken,font.serializable)
    }

    private fun saveTransitionToKValue(transition : PageTransitions){
        kVault.set(TransitionToken,transition.serializable)
    }

    private fun initTheme(){
        val value = kVault.string(ThemeToken) ?: ThemeStyle.values().first().serializable
        val theme = ThemeStyle.values().find {
            it.serializable == value
        } ?: ThemeStyle.ThemeOne
        _theme.value = theme
    }

    private fun initFont(){
        val value = kVault.string(FontToken) ?: Font.values().first().serializable
        val font = Font.values().find {
            it.serializable == value
        } ?: Font.values().first()
        _font.value = font
    }

    private fun initTransition(){
        val value = kVault.string(TransitionToken) ?: PageTransitions.values().first().serializable
        val transitions = PageTransitions.values().find {
            it.serializable == value
        } ?: PageTransitions.values().first()
        _transitions.value = transitions
    }

}


enum class PageTransitions(val serializable: String,val describe : String){
    FadeTransition(serializable = "FadeTransition", describe = "淡入淡出过渡"),
    SlideTransition(serializable = "SlideTransition", describe = "滑动过渡"),
    ScaleTransition(serializable = "ScaleTransition", describe = "大小变换过渡")
}

enum class ThemeStyle(val serializable: String){
    ThemeOne("ThemeOne"),
    ThemeTow("ThemeTow"),
    ThemeThree("ThemeThree"),
    ThemeFour("ThemeFour"),
    ThemeFive("ThemeFive"),
    ThemeSix("ThemeSix")
}

enum class Font(val serializable: String, val fontResource: FontResource){
    MulishLight("MulishLight",MR.fonts.Mulish.light),
    MadimiOne("MadimiOne",MR.fonts.MadimiOne.regular),
    EBG("EBG",MR.fonts.EBGaramond_wght.eBGaramond_wght)
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
        ThemeStyle.ThemeTow -> ComposeTheme(
            primaryInLightTheme = Color(218, 231, 203),
            onPrimaryInLightTheme = Color(198, 239, 164),
            primaryVariantInLightTheme = Color(70, 104, 45),
            secondaryInLightTheme = Color(201, 238, 208),
            onSecondaryInLightTheme = Color(100, 120, 104),
            secondaryVariantInLightTheme = Color(48, 87, 29),
            surfaceInLightTheme = Color(194, 243, 203),
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
            errorInDarkTheme = Color(83, 59, 64),
            onErrorInDarkTheme = Color(67, 16, 26),
            backgroundInDarkTheme = Color(48, 48, 48),
            onBackgroundInDarkTheme = Color(255, 255, 255)
        )

        ThemeStyle.ThemeThree -> ComposeTheme(
            primaryInLightTheme = Color(246, 242, 250),
            onPrimaryInLightTheme = Color(233, 221, 255),
            primaryVariantInLightTheme = Color(100, 86, 143),
            secondaryInLightTheme = Color(197, 169, 248),
            onSecondaryInLightTheme = Color(126, 99, 177),
            secondaryVariantInLightTheme = Color(62, 42, 99),
            surfaceInLightTheme = Color(195, 167, 248),
            onSurfaceInLightTheme = Color(0, 0, 0),
            errorInLightTheme = Color(255, 0, 8),
            onErrorInLightTheme = Color(129, 0, 28),
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
            errorInDarkTheme = Color(83, 59, 64),
            onErrorInDarkTheme = Color(67, 16, 26),
            backgroundInDarkTheme = Color(48, 48, 48),
            onBackgroundInDarkTheme = Color(255, 255, 255)
        )
        ThemeStyle.ThemeFour -> ComposeTheme(
            primaryInLightTheme = Color(181, 226, 246),
            onPrimaryInLightTheme = Color(122, 171, 193),
            primaryVariantInLightTheme = Color(66, 105, 122),
            secondaryInLightTheme = Color(138, 185, 205),
            onSecondaryInLightTheme = Color(51, 116, 145),
            secondaryVariantInLightTheme = Color(11, 49, 66),
            surfaceInLightTheme = Color(141, 205, 234),
            onSurfaceInLightTheme = Color(0, 0, 0),
            errorInLightTheme = Color(255, 0, 8),
            onErrorInLightTheme = Color(129, 0, 28),
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
            errorInDarkTheme = Color(83, 59, 64),
            onErrorInDarkTheme = Color(67, 16, 26),
            backgroundInDarkTheme = Color(48, 48, 48),
            onBackgroundInDarkTheme = Color(255, 255, 255)
        )
        ThemeStyle.ThemeFive -> ComposeTheme(
            primaryInLightTheme = Color(243, 184, 180),
            onPrimaryInLightTheme = Color(204, 130, 125),
            primaryVariantInLightTheme = Color(124, 60, 55),
            secondaryInLightTheme = Color(204, 144, 140),
            onSecondaryInLightTheme = Color(161, 54, 47),
            secondaryVariantInLightTheme = Color(94, 15, 10),
            surfaceInLightTheme = Color(228, 141, 136),
            onSurfaceInLightTheme = Color(110, 32, 27),
            errorInLightTheme = Color(255, 0, 8),
            onErrorInLightTheme = Color(129, 0, 28),
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
            errorInDarkTheme = Color(83, 59, 64),
            onErrorInDarkTheme = Color(67, 16, 26),
            backgroundInDarkTheme = Color(48, 48, 48),
            onBackgroundInDarkTheme = Color(255, 255, 255)
        )
        ThemeStyle.ThemeSix -> ComposeTheme(
            primaryInLightTheme = Color(248, 230, 178),
            onPrimaryInLightTheme = Color(161, 145, 99),
            primaryVariantInLightTheme = Color(87, 80, 19),
            secondaryInLightTheme = Color(175, 159, 108),
            onSecondaryInLightTheme = Color(143, 122, 59),
            secondaryVariantInLightTheme = Color(255, 191, 0),
            surfaceInLightTheme = Color(246, 232, 190),
            onSurfaceInLightTheme = Color(80, 60, 0),
            errorInLightTheme = Color(255, 7, 7),
            onErrorInLightTheme = Color(78, 11, 11),
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
            errorInDarkTheme = Color(83, 59, 64),
            onErrorInDarkTheme = Color(67, 16, 26),
            backgroundInDarkTheme = Color(48, 48, 48),
            onBackgroundInDarkTheme = Color(255, 255, 255)
        )



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