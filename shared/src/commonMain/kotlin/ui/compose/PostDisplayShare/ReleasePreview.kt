package ui.compose.PostDisplayShare

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import asImageBitmap
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import ui.compose.Release.ReleasePageItem

/**
 * 文本预览
 * @param modifier Modifier
 * @param text State<String>
 */
@Composable
fun ReleasePageItemTextForShow(
    modifier: Modifier,
    text : State<String>
){
    Column( modifier ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = {
                Text(
                    text = text.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        )
    }
}

/**
 * 图片预览
 * @param modifier Modifier
 * @param image MutableState<ByteArray?>
 */
@Composable
fun ReleasePageItemImageForShow(
    modifier: Modifier,
    image: MutableState<ByteArray?>
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        content = {
            Crossfade(image.value){
                if(it != null){
                    Image(
                        bitmap = it.asImageBitmap(),
                        null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    )
}

/**
 * 折线图预览
 * @param releasePageItem LineChartItem
 */
@Composable 
fun ReleasePageItemLineChartForShow(
    releasePageItem : ReleasePageItem.LineChartItem
){
    Box(
        modifier = Modifier
            .height(300.dp)
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
    ){
        Box(
            modifier = Modifier
                .height(300.dp)
                .width((100.dp + (100 * releasePageItem.lineParameters.size).dp))
        ){
            Box(Modifier){
                releasePageItem.lineParameters.let {
                    LineChart(
                        modifier = Modifier.fillMaxSize(),
                        linesParameters = listOf(
                            LineParameters(
                                label = releasePageItem.label.value,
                                data = it.map {
                                    it.y.value.toDouble()
                                },
                                lineColor = releasePageItem.gridColor.value,
                                lineType = releasePageItem.lineType.value,
                                lineShadow = releasePageItem.lineShadow.value,
                            )
                        ),
                        isGrid = releasePageItem.isGrid.value,
                        gridColor = Color.Blue,
                        xAxisData = it.map {
                            it.x.value
                        },
                        animateChart = releasePageItem.animateChart.value,
                        showGridWithSpacer = true,
                        yAxisStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray,
                        ),
                        xAxisStyle = TextStyle(
                            fontSize = 14.sp,
                            color = Color.Gray,
                            fontWeight = FontWeight.W400
                        ),
                        yAxisRange = 14,
                        oneLineChart = false,
                        gridOrientation = GridOrientation.VERTICAL
                    )
                }
            }
        }
    }
}



