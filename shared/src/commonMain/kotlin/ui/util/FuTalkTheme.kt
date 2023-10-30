package ui.util

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun FuTalkTheme(
    content:@Composable () -> Unit
){
    MaterialTheme (
        colors = MaterialTheme.colors.copy(
            primary = Color(6, 128, 215),
            primaryVariant = Color(6, 128, 215),
            surface = Color(216, 216, 238)
        )
        ,content = content
    )
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