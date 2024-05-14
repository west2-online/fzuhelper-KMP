package util.compose

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.FadeTransition
import cafe.adriel.voyager.transitions.ScaleTransition
import cafe.adriel.voyager.transitions.SlideTransition
import dao.ThemeKValueAction
import dev.icerock.moko.resources.FontResource
import dev.icerock.moko.resources.compose.fontFamilyResource
import org.example.library.MR
import org.koin.compose.koinInject

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

enum class FontStyle(val serializable: String, val fontResource: FontResource){
    MulishLight("MulishLight", MR.fonts.Mulish.light),
    MadimiOne("MadimiOne", MR.fonts.MadimiOne.regular),
    EBG("EBG", MR.fonts.EBGaramond_wght.eBGaramond_wght),
    LiuJian("LiuJian", MR.fonts.LiuJianMaoCao.regular),
    LongCang("LongCang", MR.fonts.LongCang.regular),
    ZhiMangXing("ZhiMangXing", MR.fonts.ZhiMangXing.regular)
}


@Composable
fun FuTalkTheme(

    content:@Composable () -> Unit,
){

    val setting :ThemeKValueAction = koinInject<ThemeKValueAction>()

    val themeStyle = setting.themeToken.currentValue.collectAsState()

    val themeState = remember( themeStyle.value ) {
        derivedStateOf {
            themeStyle.value.toTheme().toComposeTheme()
        }
    }

    val fontStyle = setting.fontToken.currentValue.collectAsState()
    val fontState = remember( fontStyle.value ) {
        derivedStateOf {
            fontStyle.value.toFont().fontResource
        }
    }
    LaunchedEffect(themeStyle.value){
        println("################################")
    }

    val primary by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.primaryInLightTheme else themeState.value.primaryInDarkTheme)
    val onPrimary by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.onPrimaryInLightTheme else themeState.value.onPrimaryInDarkTheme)
    val primaryVariant by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.primaryVariantInLightTheme else themeState.value.primaryVariantInDarkTheme)
    val secondary by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.secondaryInLightTheme else themeState.value.secondaryInDarkTheme)
    val onSecondary by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.onSecondaryInLightTheme else themeState.value.onSecondaryInDarkTheme)
    val secondaryVariant by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.secondaryVariantInLightTheme else themeState.value.secondaryVariantInDarkTheme)
    val surface by animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.surfaceInLightTheme else themeState.value.surfaceInDarkTheme)
    val onSurface by animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.onSurfaceInLightTheme else themeState.value.onSurfaceInDarkTheme)
    val error by animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.errorInLightTheme else themeState.value.errorInDarkTheme)
    val onError by animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.onErrorInLightTheme else themeState.value.onErrorInDarkTheme)
    val background by animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.backgroundInLightTheme else themeState.value.backgroundInDarkTheme)
    val onBackground by  animateColorAsState( if (!isSystemInDarkTheme())  themeState.value.onBackgroundInLightTheme else themeState.value.onBackgroundInDarkTheme)

    MaterialTheme (
        colors = MaterialTheme.colors.copy(
            primary = primary,
            primaryVariant = primaryVariant,
            secondary = secondary,
            secondaryVariant = secondaryVariant,
            background = background,
            surface = surface,
            error = error,
            onPrimary = onPrimary,
            onSecondary = onSecondary,
            onBackground = onBackground,
            onSurface = onSurface,
            onError = onError,
            isLight = isSystemInDarkTheme()
        ),
        content = content,
        typography = MaterialTheme.typography.copy(
            h1 = MaterialTheme.typography.h1.copy(fontFamily = fontFamilyResource(fontState.value)),
            h2 = MaterialTheme.typography.h2.copy(fontFamily = fontFamilyResource(fontState.value)),
            h3 = MaterialTheme.typography.h3.copy(fontFamily = fontFamilyResource(fontState.value)),
            h4 = MaterialTheme.typography.h4.copy(fontFamily = fontFamilyResource(fontState.value)),
            h5 = MaterialTheme.typography.h5.copy(fontFamily = fontFamilyResource(fontState.value)),
            h6 = MaterialTheme.typography.h6.copy(fontFamily = fontFamilyResource(fontState.value)),
            subtitle1 = MaterialTheme.typography.subtitle1.copy(fontFamily = fontFamilyResource(fontState.value)),
            subtitle2 = MaterialTheme.typography.subtitle2.copy(fontFamily = fontFamilyResource(fontState.value)),
            body1 = MaterialTheme.typography.body1.copy(fontFamily = fontFamilyResource(fontState.value)),
            body2 = MaterialTheme.typography.body2.copy(fontFamily = fontFamilyResource(fontState.value)),
            button = MaterialTheme.typography.button.copy(fontFamily = fontFamilyResource(fontState.value)),
            caption = MaterialTheme.typography.caption.copy(fontFamily = fontFamilyResource(fontState.value)),
            overline = MaterialTheme.typography.overline.copy(fontFamily = fontFamilyResource(fontState.value))
        )
    )
}

fun String?.toFont(): FontStyle {
    this?:return FontStyle.entries.first()
    return FontStyle.entries.find {
        it.serializable == this
    }?:FontStyle.entries.first()
}

fun String?.toTheme(): ThemeStyle {
    this?:return ThemeStyle.entries.first()
    return ThemeStyle.entries.find {
        it.serializable == this
    }?: ThemeStyle.entries.first()
}


interface Theme {
    val primary: Color
    val primaryVariant: Color
    val secondary: Color
    val secondaryVariant: Color
    val background: Color
    val surface: Color
    val error: Color
    val onPrimary: Color
    val onSecondary: Color
    val onBackground: Color
    val onSurface: Color
    val onError: Color
    val isLight: Boolean

    class LightTheme(
        override val primary: Color,
        override val onPrimary: Color,
        override val primaryVariant: Color,
        override val secondary: Color,
        override val onSecondary: Color,
        override val secondaryVariant: Color,
        override val surface: Color,
        override val onSurface: Color,
        override val error: Color,
        override val onError: Color,
        override val background: Color,
        override val onBackground: Color,
        override val isLight: Boolean = true
    ) : Theme

    class DarkTheme(
        override val primary: Color,
        override val onPrimary: Color,
        override val primaryVariant: Color,
        override val secondary: Color,
        override val onSecondary: Color,
        override val secondaryVariant: Color,
        override val surface: Color,
        override val onSurface: Color,
        override val error: Color,
        override val onError: Color,
        override val background: Color,
        override val onBackground: Color,
        override val isLight: Boolean = true
    ) : Theme
}

fun Modifier.shimmerLoadingAnimation(
    widthOfShadowBrush: Int = 500,
    angleOfAxisY: Float = 270f,
    durationMillis: Int = 1000,
    colorList : List<Color> = listOf(
        Color.Black.copy(alpha = 0.3f),
        Color.Black.copy(alpha = 0.5f),
        Color.Black.copy(alpha = 0.7f),
        Color.Black.copy(alpha = 0.5f),
        Color.Black.copy(alpha = 0.3f),
    )
): Modifier {
    return composed {
        val transition = rememberInfiniteTransition(label = "")
        val translateAnimation = transition.animateFloat(
            initialValue = 0f,
            targetValue = (durationMillis + widthOfShadowBrush).toFloat(),
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMillis,
                    easing = LinearEasing,
                ),
                repeatMode = RepeatMode.Restart,
            ),
            label = "Shimmer loading animation",
        )

        this.background(
            brush = Brush.linearGradient(
                colors = colorList,
                start = Offset(x = translateAnimation.value - widthOfShadowBrush, y = 0.0f),
                end = Offset(x = translateAnimation.value, y = angleOfAxisY),
            ),
        )
    }
}



@Composable
fun SettingTransitions(navigator: Navigator){
    val setting =koinInject<ThemeKValueAction>()
    val transitions = setting.transitionToken.currentValue.collectAsState()
    val currentTransitions = remember(transitions.value) {
        derivedStateOf {
            transitions.value.toTransitions()
        }
    }
    LaunchedEffect(transitions.value){
        println("=====================================")
    }
    when(currentTransitions.value){
        PageTransitions.FadeTransition -> FadeTransition(navigator)
        PageTransitions.SlideTransition -> SlideTransition(navigator)
        PageTransitions.ScaleTransition -> ScaleTransition(navigator)
    }
}

fun String?.toTransitions(): PageTransitions {
    this?:return PageTransitions.entries.first()
    return PageTransitions.entries.find {
        it.serializable == this
    }?: PageTransitions.entries.first()
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


fun Colors.copy(theme: Theme): Colors {
    return this.copy(
        primary = theme.primary,
        primaryVariant = theme.primaryVariant,
        secondary = theme.secondary,
        secondaryVariant = theme.secondaryVariant,
        background = theme.background,
        surface = theme.surface,
        error = theme.error,
        onPrimary = theme.onPrimary,
        onSecondary = theme.onSecondary,
        onBackground = theme.onBackground,
        onSurface = theme.onSurface,
        onError = theme.onError,
        isLight = theme.isLight
    )
}

