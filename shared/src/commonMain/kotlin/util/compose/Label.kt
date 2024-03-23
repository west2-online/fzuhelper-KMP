package util.compose

import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sqrt

@Composable
fun Label(
    string: String,
){
    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(end = 5.dp, bottom = 5.dp),
        shape = RoundedCornerShape(20)
    ) {
//        val string: String = stringResource(MR.strings.my_string)
        Text(
            string,
            fontSize = 10.sp,
            modifier = Modifier
                .padding(vertical = 5.dp, horizontal = 10.dp),
        )
    }
}

fun Modifier.loadAction():Modifier{
    return this.composed {
        val s = androidx.compose.animation.core.Animatable(0f)
        LaunchedEffect(Unit){
            s.animateTo(
                1f,
                tween(1000)
            )
        }
        val text = rememberTextMeasurer()
        this.drawWithContent {
            val path = Path()
            val padding = 222
            val otherPadding = padding.toFloat()/2*(sqrt(3.0)).toFloat()
            val list = listOf(
                Offset(center.x+otherPadding,center.y - padding/2),
                Offset(center.x,center.y - padding),
                Offset(center.x-otherPadding,center.y-padding/2),
                Offset(center.x-otherPadding,center.y+padding/2),
                Offset(center.x,center.y + padding),
                Offset(center.x+otherPadding,center.y + padding/2),
                Offset(center.x+otherPadding,center.y - padding/2),
                Offset(center.x,center.y),
                Offset(center.x,center.y + padding),
            );

//            list.forEachIndexed range@ { index, offset ->
//                if(index == list.size-1){
//                   return@range
//                }
//                drawLine(Color.Black,offset,
//                    Offset(offset.x + (list[index+1].x - offset.x)*s.value,offset.y + (list[index+1].y - offset.y)*s.value)
//                            ,strokeWidth = 10f,)
//            }
            val path1 = Path()
            val path2 = Path()
            val path1List = listOf(
                Offset(center.x+otherPadding,center.y - padding/2),
                Offset(center.x,center.y - padding),
                Offset(center.x-otherPadding,center.y-padding/2),
                Offset(center.x-otherPadding,center.y+padding/2),
                Offset(center.x,center.y + padding),
                Offset(center.x,center.y ),
                Offset(center.x+otherPadding,center.y - padding/2),
            )
            val path2List = listOf(
                Offset(center.x+otherPadding,center.y - padding/2),
                Offset(center.x,center.y),
                Offset(center.x,center.y + padding),
                Offset(center.x+otherPadding,center.y + padding/2),
                Offset(center.x+otherPadding,center.y - padding/2),
            )

            path1List.forEachIndexed { index, offset ->
                if(index == 0){
                    path1.moveTo(offset.x,offset.y)
                }else if(index == path1List.size - 1){
                    path1.lineTo(offset.x,offset.y)
                    path1.close()
                }else{
                    path1.lineTo(offset.x,offset.y)
                }
            }
            path2List.forEachIndexed { index, offset ->
                if(index == 0){
                    path2.moveTo(offset.x,offset.y)
                }else if(index == path1List.size - 1){
                    path2.lineTo(offset.x,offset.y)
                    path2.close()
                }else{
                    path2.lineTo(offset.x,offset.y)
                }
            }

            clipRect (bottom = s.value * padding * 4 + size.height/2 - 2*padding){
                drawPath(path1, brush = Brush.linearGradient(
                    listOf(
                        Color(23,65,217),
                        Color(21,77,222),
                        Color(140,157,202),
                    ),
                    start = Offset(center.x,center.y - padding),
                    end = Offset(center.x,center.y + padding)
                ))


            }
            clipRect(top = center.y + padding - s.value * padding/2*3 , bottom = size.height) {
                drawPath(path2,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(38,185,176),
                            Color(202,234,232),
                        ),
                        start = Offset(center.x,center.y + padding),
                        end = Offset(center.x+otherPadding,center.y - padding/2)
                    )
                )
            }
            rotate(330f,Offset(center.x,center.y )){
                val data = text.measure("FuTalk")
                drawText(
                    data,
                    topLeft = Offset(center.x,center.y),
                )
            }

        }
    }
}

@Composable
fun ThemeCard(
    cardModifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.medium,
    backgroundColor: Color = MaterialTheme.colors.surface,
    contentColor: Color = contentColorFor(backgroundColor),
    border: BorderStroke? = null,
    elevation: Dp = 1.dp,
    columnModifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card (
        cardModifier,
        shape,
        backgroundColor,
        contentColor,
        border,
        elevation,
    ) {
        Column (
            modifier = columnModifier,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        ){
            content()
        }
    }
}
