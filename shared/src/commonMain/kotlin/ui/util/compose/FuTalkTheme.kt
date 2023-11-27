package ui.util.compose

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.icerock.moko.resources.compose.fontFamilyResource
import org.example.library.MR

@Composable
fun FuTalkTheme(
    content:@Composable () -> Unit
){
    MaterialTheme (
        colors =
        if (isSystemInDarkTheme()) MaterialTheme.colors.copy(
            Theme.MyTheme()
        ) else MaterialTheme.colors.copy(Theme.DarkModeTheme()),
        content = content,
        typography = MaterialTheme.typography.copy(
            body1 = TextStyle(
                fontFamily = fontFamilyResource(MR.fonts.Mulish.light)
            )
        )
    )
}

fun Colors.copy(theme:Theme): Colors {
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

interface Theme{
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

    class MyTheme : Theme {
        override val primary: Color = Color(199, 231, 255) // RGB(197, 225, 165)
        override val primaryVariant: Color = Color(51, 113, 243) // RGB(174, 213, 129)
        override val secondary: Color = Color(199, 231, 255) // RGB(199, 231, 255)
        override val secondaryVariant: Color = Color(220, 231, 117) // RGB(220, 231, 117)
        override val background: Color = Color(255, 255, 255) // RGB(253, 253, 246)
        override val surface: Color = Color(241, 244, 249) // RGB(241, 244, 249)
        override val error: Color = Color(239, 83, 80) // RGB(239, 83, 80)
        override val onPrimary: Color = Color(0, 0, 0) // RGB(0, 0, 0)
        override val onSecondary: Color = Color(0, 0, 0) // RGB(0, 0, 0)
        override val onBackground: Color = Color(0, 0, 0) // RGB(0, 0, 0)
        override val onSurface: Color = Color(0, 0, 0) // RGB(0, 0, 0)
        override val onError: Color = Color(255, 255, 255) // RGB(255, 255, 255)
        override val isLight: Boolean = true
    }
    class DarkModeTheme : Theme {
        override val primary: Color = Color(0xFFBB86FC)
        override val primaryVariant: Color = Color(0xFF3700B3)
        override val secondary: Color = Color(0xFF03DAC6)
        override val secondaryVariant: Color = Color(0xFF018786)
        override val background: Color = Color(0xFF121212)
        override val surface: Color = Color(0xFF121212)
        override val error: Color = Color(0xFFCF6679)
        override val onPrimary: Color = Color(0xFF000000)
        override val onSecondary: Color = Color(0xFF000000)
        override val onBackground: Color = Color(0xFFFFFFFF)
        override val onSurface: Color = Color(0xFFFFFFFF)
        override val onError: Color = Color(0xFF000000)
        override val isLight: Boolean = false
    }
}

//fun Modifier.shimmerLoadingAnimation(): Modifier {
//    return composed {
//
//        val shimmerColors = listOf(
//            Color.Black.copy(alpha = 0.3f),
//            Color.Black.copy(alpha = 0.5f),
//            Color.Black.copy(alpha = 1.0f),
//            Color.Black.copy(alpha = 0.5f),
//            Color.Black.copy(alpha = 0.3f),
//        )
//
//        return@composed this.background(
//            brush = Brush.linearGradient(
//                colors = shimmerColors,
//                start = Offset(x = 100f, y = 0.0f),
//                end = Offset(x = 400f, y = 270f),
//            ),
//        )
//    }
//}

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