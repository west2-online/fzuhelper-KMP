package ui.util.compose

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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import dev.icerock.moko.resources.compose.colorResource
import dev.icerock.moko.resources.compose.fontFamilyResource
import org.example.library.MR

@Composable
fun FuTalkTheme(
    content:@Composable () -> Unit
){
    val primary by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.primary_dark) else colorResource( MR.colors.primary_light) )
    val onPrimary by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.onPrimary_dark) else colorResource( MR.colors.onPrimary_light) )
    val primaryVariant by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.primaryVariant_dark) else colorResource( MR.colors.primaryVariant_light) )
    val secondary by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.secondary_dark) else colorResource( MR.colors.secondary_light) )
    val onSecondary by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.onSecondary_dark) else colorResource( MR.colors.onSecondary_light) )
    val secondaryVariant by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.secondaryVariant_dark) else colorResource( MR.colors.secondaryVariant_light) )
    val surface by animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.surface_dark) else colorResource( MR.colors.surface_light) )
    val onSurface by animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.onSurface_dark) else colorResource( MR.colors.onSurface_light) )
    val error by animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.error_dark) else colorResource( MR.colors.error_light) )
    val onError by animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.onError_dark) else colorResource( MR.colors.onError_light) )
    val background by animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.background_dark) else colorResource( MR.colors.background_light) )
    val onBackground by  animateColorAsState(if (isSystemInDarkTheme()) colorResource(MR.colors.onBackground_dark) else colorResource( MR.colors.onBackground_light) )
    val lightTheme = Theme.LightTheme(
        colorResource(MR.colors.primary_light),
        colorResource(MR.colors.onPrimary_light),
        colorResource(MR.colors.primaryVariant_light),
        colorResource(MR.colors.secondary_light),
        colorResource(MR.colors.onSecondary_light),
        colorResource(MR.colors.secondaryVariant_light),
        colorResource(MR.colors.surface_light),
        colorResource(MR.colors.onSurface_light),
        colorResource(MR.colors.error_light),
        colorResource(MR.colors.onError_light),
        colorResource(MR.colors.background_light),
        colorResource(MR.colors.onBackground_light),
    )
    val darkTheme = Theme.LightTheme(
        colorResource(MR.colors.primary_dark),
        colorResource(MR.colors.onPrimary_dark),
        colorResource(MR.colors.primaryVariant_dark),
        colorResource(MR.colors.secondary_dark),
        colorResource(MR.colors.onSecondary_dark),
        colorResource(MR.colors.secondaryVariant_dark),
        colorResource(MR.colors.surface_dark),
        colorResource(MR.colors.onSurface_dark),
        colorResource(MR.colors.error_dark),
        colorResource(MR.colors.onError_dark),
        colorResource(MR.colors.background_dark),
        colorResource(MR.colors.onBackground_dark),
    )
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

    class LightTheme(
        override val primary: Color  ,
        override val onPrimary: Color ,
        override val primaryVariant: Color ,
        override val secondary: Color ,
        override val onSecondary: Color ,
        override val secondaryVariant: Color ,
        override val surface: Color,
        override val onSurface: Color,
        override val error: Color,
        override val onError: Color,
        override val background: Color,
        override val onBackground: Color ,
        override val isLight: Boolean = true
    ):Theme
    class DarkTheme(
        override val primary: Color  ,
        override val onPrimary: Color ,
        override val primaryVariant: Color ,
        override val secondary: Color ,
        override val onSecondary: Color ,
        override val secondaryVariant: Color ,
        override val surface: Color,
        override val onSurface: Color,
        override val error: Color,
        override val onError: Color,
        override val background: Color,
        override val onBackground: Color ,
        override val isLight: Boolean = true
    ):Theme
//    class LightTheme : Theme {
//        override val primary: Color = Color( 11, 200, 242) // RGB(197, 225, 165)
//        override val primaryVariant: Color = Color(51, 113, 243) // RGB(174, 213, 129)
//        override val secondary: Color = Color( 11, 200, 242) // RGB(199, 231, 255)
//        override val secondaryVariant: Color = Color(220, 231, 117) // RGB(220, 231, 117)
//        override val background: Color = Color(255, 255, 255) // RGB(253, 253, 246)
//        override val surface: Color = Color(241, 244, 249) // RGB(241, 244, 249)
//        override val error: Color = Color(239, 83, 80) // RGB(239, 83, 80)
//        override val onPrimary: Color = Color(0, 0, 0) // RGB(0, 0, 0)
//        override val onSecondary: Color = Color(0, 0, 0) // RGB(0, 0, 0)
//        override val onBackground: Color = Color(0, 0, 0) // RGB(0, 0, 0)
//        override val onSurface: Color = Color(0, 0, 0) // RGB(0, 0, 0)
//        override val onError: Color = Color(255, 255, 255) // RGB(255, 255, 255)
//        override val isLight: Boolean = true
//    }
//    class DarkModeTheme : Theme {
//        override val primary: Color = Color(22, 27, 34)
//        override val primaryVariant: Color = Color.Black
//        override val secondary: Color = Color.Black
//        override val secondaryVariant: Color = Color.Black
//        override val background: Color = Color.Black
//        override val surface: Color = Color.Black
//        override val error: Color = Color.Black
//        override val onPrimary: Color = Color(218, 218, 239,100)
//        override val onSecondary: Color = Color.White
//        override val onBackground: Color = Color(56, 59, 67)
//        override val onSurface: Color = Color.White
//        override val onError: Color = Color(255, 50, 114)
//        override val isLight: Boolean = false
//    }

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