package ui.compose.Post

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import config.BaseUrlConfig
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ImageContent(
    imageData: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    ){
        KamelImage(
            resource = asyncPainterResource("${BaseUrlConfig.PostImage}/${imageData}"),
            null,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun TextContent(
    text :String
){
    Text(text)
}

@Composable
fun ChartContent(

){
    val testLineParameters: List<LineParameters> = listOf(
        LineParameters(
            label = "revenue",
            data = listOf(70.0, 00.0, 50.33, 40.0, 100.500, 50.0),
            lineColor = Color.Gray,
            lineType = LineType.CURVED_LINE,
            lineShadow = true,
        ),
        LineParameters(
            label = "Earnings",
            data = listOf(60.0, 80.6, 40.33, 86.232, 88.0, 90.0),
            lineColor = Color(0xFFFF7F50),
            lineType = LineType.DEFAULT_LINE,
            lineShadow = true
        ),
        LineParameters(
            label = "Earnings",
            data = listOf(1.0, 40.0, 11.33, 55.23, 1.0, 100.0),
            lineColor = Color(0xFF81BE88),
            lineType = LineType.CURVED_LINE,
            lineShadow = false,
        )
    )
    LineChart(
        modifier = Modifier.fillMaxWidth().wrapContentHeight(),
        linesParameters = testLineParameters,
        isGrid = true,
        gridColor = Color.Blue,
        xAxisData = listOf("2015", "2016", "2017", "2018", "2019", "2020"),
        showGridWithSpacer = true,
        yAxisRange = 14,
        oneLineChart = false,
        gridOrientation = GridOrientation.VERTICAL
    )
}

data class ChartData(
    val xAxisData : List<String>,
    val isGrid : Boolean,
    val gridColor :String,
    val lineParameters : List<LineParametersData>,
    val height: Int,
    val with : Float
)

data class LineParametersData(
    val label: String,
    val data : List<Float>,
    val lineType : Int,
    val color :String
)