package ui.compose.Massage

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun MassageList(
    modifier: Modifier,
    navigateToMassageDetail:(String)->Unit
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Box(modifier = modifier){
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ){
            items(310){
                MassageItem(
                    modifier = Modifier
                        .padding(vertical = 3.dp)
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable {
                            navigateToMassageDetail.invoke("")
                        },
                    navigateToMassageDetail = navigateToMassageDetail
                )
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .wrapContentSize()
//                .padding(
//                    if (isExpanded) 10.dp else 0.dp
//                )
                .offset (
                    (-10).dp,
                    (-10).dp
                )
        ){
            val remote = animateFloatAsState(
                if (isExpanded) 90f else 0f,
                animationSpec = tween(300)
            )
            val width = animateDpAsState(
                if (isExpanded) 300.dp else 0.dp,
                animationSpec = tween(300)
            )
            LazyRow(
                modifier = Modifier
                    .width(width.value)
                    .height(40.dp)
                    .padding(end = 20.dp)
                    .clip(RoundedCornerShape(topStart = 10.dp, bottomStart = 10.dp))
                    .background(Color.Red)
                    .padding(
                        start = 10.dp,end = 30.dp,top = 10.dp, bottom = 10.dp
                    )
                    .align(Alignment.CenterEnd)
            ) {
                items(30) {
                    Box(modifier = Modifier.padding(end = 10.dp).fillMaxHeight().width(30.dp).background(Color.Blue))
                }
            }
            FloatingActionButton(
                onClick = {
                    isExpanded = !isExpanded
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    modifier = Modifier
                        .rotate(remote.value)
                        .size(35.dp),
                    contentDescription = null
                )
            }
        }
    }
}

@Composable
fun MassageItem(
    modifier: Modifier,
    navigateToMassageDetail: (String) -> Unit,
){
    Row (
        modifier = modifier
    ){
        val text = buildAnnotatedString {
            withStyle(
                style = SpanStyle(
                    brush = Brush.horizontalGradient(colors = listOf(Color.Green,Color.Red)),
                    fontWeight = FontWeight.Bold
                )
            ) {
                append("Text on Canvas️")
            }
        }
        val testText = "1"
        val textMeasure = rememberTextMeasurer()
        val size = textMeasure.measure(
            testText,
            style = TextStyle(fontSize = 10.sp)
        ).size
        val padding = 20f
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
            null,
            modifier = Modifier
                .padding(end = 10.dp)
                .fillMaxHeight()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10))
                .drawWithContent {
                    drawContent()
                    drawRoundRect(
                        color = Color.Red,
                        size = size.toSize().copy(size.width+ padding ),
                        cornerRadius = CornerRadius(5f),
                        topLeft = Offset(
                            this.size.width - 10f - size.width - padding,
                            10f
                        )
                    )
                    drawText(
                        textMeasurer = textMeasure,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 3,
                        style = TextStyle(fontSize = 10.sp),
                        size = size.toSize(),
                        text = testText,
                        topLeft = Offset(padding/2+ this.size.width - 10f - size.width - padding,10f)
                    )
                },
            contentScale = ContentScale.FillBounds
        )
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            val data = getRandoms()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Box(
                    modifier = Modifier
                        .wrapContentSize()
                        .drawBehind {
                            drawRoundRect(
                                brush = Brush.horizontalGradient(data.colorList),
                                cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx()),
                                size = this.size
                            )
                        }
                        .padding(horizontal = 5.dp, vertical = 2.dp)

                ){
                    Text(
                        data.tag,
                        maxLines = 1,
                        modifier = Modifier
                            .wrapContentSize(),
                        fontSize = 10.sp
                    )
                }
                Text(
                    text,
                    maxLines = 1,
                    modifier = Modifier
                        .wrapContentSize()
                        .align(Alignment.CenterVertically)
                )
            }
            Text(
                text,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                maxLines = 1
            )
        }
    }
}


enum class MassageLabel(val tag : String,val colorList : List<Color>){
    League("社团", listOf(Color.Green,Color(216, 216, 238))),
    Game("比赛", listOf(Color(254, 117, 9),Color(216, 216, 238)))

}
fun getRandoms(): MassageLabel {
    return MassageLabel.values().random()
}