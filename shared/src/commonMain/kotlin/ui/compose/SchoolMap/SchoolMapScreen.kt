package ui.compose.SchoolMap

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.jetbrains.compose.resources.ExperimentalResourceApi

@OptIn(ExperimentalResourceApi::class)
@Composable
fun SchoolMapScreen(modifier: Modifier = Modifier, url: String, onClick: () -> Unit = {}) {

  var scale by remember { mutableStateOf(1f) }
  var offset by remember { mutableStateOf(Offset.Zero) }
  val state = rememberTransformableState { zoomChange, _, _ ->
    scale = (zoomChange * scale).coerceAtLeast(1f)
  }
  Surface(
    color = Color.DarkGray,
    modifier =
      modifier
        .fillMaxSize()
        //            .aspectRatio(0.01f)
        .pointerInput(Unit) {
          detectTapGestures(
            //                    onDoubleTap = {
            //                        scale = 1f
            //                        offset = Offset.Zero
            //                    },
          )
        },
  ) {
    LazyHorizontalGrid(
      rows = GridCells.Fixed(8),
      userScrollEnabled = true,
      modifier =
        Modifier.fillMaxHeight()
          .wrapContentWidth()
          .transformable(state = state)
          .graphicsLayer(
            scaleX = scale,
            scaleY = scale,
            translationX = offset.x * scale,
            translationY = offset.y * scale,
          )
          .pointerInput(Unit) {
            // 旋转/缩放/平移手势监听
            detectTransformGestures { centroid, pan, zoom, rotation ->
              scale *= zoom
              offset += pan
            }
          },
    ) {
      items(96) {
        KamelImage(
          asyncPainterResource(
            "https://map.fzu.edu.cn/map?lyrs=last&x=${18750+it/8}&y=${9376+it%8}&z=15"
          ),
          contentDescription = "",
          modifier = Modifier.fillMaxWidth().aspectRatio(1f),
          onFailure = { Box(modifier = Modifier.fillMaxSize().background(Color.Red)) },
        )
      }
    }
  }
}
