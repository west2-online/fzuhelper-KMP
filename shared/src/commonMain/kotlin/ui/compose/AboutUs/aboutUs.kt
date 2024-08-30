package ui.compose.AboutUs

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlin.jvm.Transient
import kotlin.math.sqrt
import org.jetbrains.compose.ui.tooling.preview.Preview
import ui.compose.Main.MainItems
import util.compose.OwnMarkdown
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl

/**
 * 关于我们的ui
 *
 * @param modifier Modifier
 */
@Preview
@Composable
fun AboutUsScreen(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.verticalScroll(rememberScrollState()),
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    val rate = androidx.compose.animation.core.Animatable(1f)
    //        LaunchedEffect(Unit){
    //            withContext(Dispatchers.IO){
    //                rate.animateTo(
    //                    1f,
    //                    tween(1000)
    //                )
    //            }
    //        }
    val text = rememberTextMeasurer()
    Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
      Canvas(modifier = Modifier.align(Alignment.Center).fillMaxSize(0.5f)) {
        val padding = size.height / 2
        val otherPadding = padding / 2 * (sqrt(3.0)).toFloat()
        val path1 = Path()
        val path2 = Path()
        val path1List =
          listOf(
            Offset(center.x + otherPadding, center.y - padding / 2),
            Offset(center.x, center.y - padding),
            Offset(center.x - otherPadding, center.y - padding / 2),
            Offset(center.x - otherPadding, center.y + padding / 2),
            Offset(center.x, center.y + padding),
            Offset(center.x, center.y),
            Offset(center.x + otherPadding, center.y - padding / 2),
          )
        val path2List =
          listOf(
            Offset(center.x + otherPadding, center.y - padding / 2),
            Offset(center.x, center.y),
            Offset(center.x, center.y + padding),
            Offset(center.x + otherPadding, center.y + padding / 2),
            Offset(center.x + otherPadding, center.y - padding / 2),
          )
        path1List.forEachIndexed { index, offset ->
          when (index) {
            0 -> {
              path1.moveTo(offset.x, offset.y)
            }
            path1List.size - 1 -> {
              path1.lineTo(offset.x, offset.y)
              path1.close()
            }
            else -> {
              path1.lineTo(offset.x, offset.y)
            }
          }
        }
        path2List.forEachIndexed { index, offset ->
          when (index) {
            0 -> {
              path2.moveTo(offset.x, offset.y)
            }
            path1List.size - 1 -> {
              path2.lineTo(offset.x, offset.y)
              path2.close()
            }
            else -> {
              path2.lineTo(offset.x, offset.y)
            }
          }
        }

        clipRect(bottom = rate.value * padding * 4 + size.height / 2 - 2 * padding) {
          drawPath(
            path1,
            brush =
              Brush.linearGradient(
                listOf(Color(23, 65, 217), Color(21, 77, 222), Color(140, 157, 202)),
                start = Offset(center.x, center.y - padding),
                end = Offset(center.x, center.y + padding),
              ),
          )
        }
        clipRect(top = center.y + padding - rate.value * padding / 2 * 3, bottom = size.height) {
          drawPath(
            path2,
            brush =
              Brush.linearGradient(
                listOf(Color(38, 185, 176), Color(202, 234, 232)),
                start = Offset(center.x, center.y + padding),
                end = Offset(center.x + otherPadding, center.y - padding / 2),
              ),
          )
        }
        rotate(330f, Offset(center.x, center.y)) {
          val data = text.measure("FuTalk")
          drawText(data, topLeft = Offset(center.x, center.y))
        }
      }
    }
    OwnMarkdown(
      content = markdown,
      modifier = Modifier.padding(bottom = 10.dp).fillMaxWidth(0.9f).wrapContentHeight(),
    )
  }
}

/** Markdown 显示的markdown */
val markdown =
  """
    
# 欢迎使用FuTalk🤗

## **👏初创成员**

- 沈轻腾

## **👏开发成员**

- 沈轻腾
- 徐煜晖

## **👻FuTalk与学校关系**

 **FuTalk** 属于私人开发，未得到 **福州大学** 的任何支持，并没有关系，您在 **FuTalk** 上的信息并不会共享给学校，大家可以大胆发言哦 😉😉

## **🤠关于软件**

该软件仍在开发阶段，我们仍在积极和各个社团展开合作，并完善软件，敬请期待

## **👀反馈**

您的反馈对我们非常重要，任何关于软件的反馈都可以在软件的 **反馈模块** 添加反馈 或 在 **GitHub** 上向我们提出issue 🧐🧐

## **🤝关于西二在线**

本软件复用了极少部分的fuu代码，同时表达对于所有fuu的开发者和维护者们的尊敬，他们在没有回报的情况下开发了fuu,并且数年的的持续坚持，Respect！👍👍👍

## **🌏Github地址**

https://github.com/Futalker

## **✔FuTalk官方网站**

https://futalker.github.io

"""
    .trimIndent()

/**
 * 关于我们的一级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @property options TabOptions
 * @constructor
 */
class AboutUsVoyagerScreen(
  @Transient private val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Tab {
  @Composable
  override fun Content() {
    AboutUsScreen(modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl))
  }

  override val options: TabOptions
    @Composable
    get() {
      val title = MainItems.POST.tag
      val icon = rememberVectorPainter(MainItems.POST.selectImageVector)
      return remember { TabOptions(index = 0u, title = title, icon = icon) }
    }
}
