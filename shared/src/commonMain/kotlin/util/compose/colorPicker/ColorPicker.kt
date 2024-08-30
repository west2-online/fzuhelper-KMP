package util.compose.colorPicker

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ColorPicker() {
  Box(modifier = Modifier.fillMaxSize()) {
    val pressOffset = remember { mutableStateOf(Offset.Zero) }
    val interactionSource = remember { MutableInteractionSource() }
    val currentColor = remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()
    Canvas(
      modifier =
        Modifier.height(40.dp)
          .fillMaxWidth()
          .clip(RoundedCornerShape(50))
          .emitDragGesture(interactionSource)
    ) {
      //            fun pointToHue(pointX: Float): Color {
      //                val width = size.width
      //                val di = pointX/width
      //                val pair = when {
      //                    di < 0.2 -> {
      //                        Pair(Color.White,Color.Red).let {
      //                            it.first.toArgb()
      //                        }
      //                    }
      //                    0.4 > di && di > 0.2 -> Pair(Color.Red, Color.Green)
      //                    0.6 > di && di > 0.4 -> Pair(Color.Green, Color.Cyan)
      //                    0.8 > di && di > 0.6 -> Pair(Color.Cyan,Color.Blue)
      //                    1.0 > di && di > 0.8 -> Pair(Color.Blue,Color.Black)
      //                    else -> Pair(Color.Blue,Color.Black)
      //                }
      //                return
      //            }
      val drawScopeSize = size
      scope.collectForPress(interactionSource) { pressPosition ->
        val pressPos = pressPosition.x.coerceIn(0f..drawScopeSize.width)
        pressOffset.value = Offset(pressPos, 0f)
        //                val selectedHue = pointToHue(pressPos)

      }
      drawRoundRect(
        brush =
          Brush.linearGradient(
            listOf(Color.White, Color.Red, Color.Green, Color.Cyan, Color.Blue, Color.Cyan),
            start = Offset.Zero,
            end = Offset.Infinite,
          ),
        topLeft = Offset(0f, 0f),
        size = size,
      )
      drawCircle(
        Color.White,
        radius = size.height / 2,
        center = Offset(pressOffset.value.x, size.height / 2),
        style = Stroke(width = 2.dp.toPx()),
      )
    }
  }
}

@Composable
fun SatValPanel(hue: Float, setSatVal: (Float, Float) -> Unit) {
  val interactionSource = remember { MutableInteractionSource() }
  val scope = rememberCoroutineScope()
  var sat: Float
  var value: Float
  val pressOffset = remember { mutableStateOf(Offset.Zero) }
}

private fun Modifier.emitDragGesture(interactionSource: MutableInteractionSource): Modifier =
  composed {
    val scope = rememberCoroutineScope()
    pointerInput(Unit) {
        detectDragGestures { input, _ ->
          scope.launch { interactionSource.emit(PressInteraction.Press(input.position)) }
        }
      }
      .clickable(interactionSource, null) {}
  }

fun CoroutineScope.collectForPress(
  interactionSource: InteractionSource,
  setOffset: (Offset) -> Unit,
) {
  launch {
    interactionSource.interactions.collect { interaction ->
      (interaction as? PressInteraction.Press)?.pressPosition?.let(setOffset)
    }
  }
}
