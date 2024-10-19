package util.compose

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope

fun tagPathPart1(): Path {
  return Path().apply {
    moveTo(0.5f, 13.3417f)
    lineTo(0.5f, 13.945f)
    lineTo(1.5f, 13.945f)
    lineTo(1.5f, 13.3417f)
    lineTo(0.5f, 13.3417f)
    close()
  }
}

fun tagPathPart2(): Path {
  return Path().apply {
    moveTo(2f, 0f)
    lineTo(31.7997f, 0f)
    lineTo(27.9745f, 6.5f)
    lineTo(31.7997f, 13f)
    lineTo(2.0301f, 13f)
    cubicTo(1.5219f, 12.9831f,
      1.0804f, 13.0664f,
      0.7056f, 13.2499f)
    cubicTo(0.3309f, 13.4333f,
      0.0956f, 13.8429f,
      0f, 14.4785f)
    lineTo(0f, 2f)
    cubicTo(0f, 0.8954f,
      0.8954f, 0f,
      2f, 0f)
    close()
  }
}

fun DrawScope.drawTag(color: Long){
  drawPath(
    path = tagPathPart1(),
    color = Color(0xFF000000),
    style = androidx.compose.ui.graphics.drawscope.Fill,
  )

  drawPath(
    path = tagPathPart2(),
    color = Color(color),
    style = androidx.compose.ui.graphics.drawscope.Fill
  )
}
