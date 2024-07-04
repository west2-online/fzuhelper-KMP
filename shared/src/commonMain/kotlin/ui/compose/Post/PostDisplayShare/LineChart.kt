package ui.compose.Post.PostDisplayShare

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import io.github.koalaplot.core.ChartLayout
import io.github.koalaplot.core.Symbol
import io.github.koalaplot.core.legend.FlowLegend
import io.github.koalaplot.core.legend.LegendLocation
import io.github.koalaplot.core.line.LinePlot
import io.github.koalaplot.core.style.LineStyle
import io.github.koalaplot.core.util.ExperimentalKoalaPlotApi
import io.github.koalaplot.core.util.VerticalRotation
import io.github.koalaplot.core.util.generateHueColorPalette
import io.github.koalaplot.core.util.rotateVertically
import io.github.koalaplot.core.xygraph.CategoryAxisModel
import io.github.koalaplot.core.xygraph.DefaultPoint
import io.github.koalaplot.core.xygraph.FloatLinearAxisModel
import io.github.koalaplot.core.xygraph.XYGraph
import io.github.koalaplot.core.xygraph.XYGraphScope
import kotlinx.serialization.Serializable
import kotlin.math.ceil


@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
@Suppress("MagicNumber")
fun XYChart(
    thumbnail: Boolean,
    modifier: Modifier = Modifier,
    data: LineChartData
) {
    val colorMap = remember {
        data.createColorMap()
    }
    if (data.isAvailable()){
        Box(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .then(
                    if(data.xData.size < 8){
                        Modifier.fillMaxWidth()
                    }else{
                        Modifier .horizontalScroll(rememberScrollState())
                    }
                )
        ) {
            ChartLayout(
                modifier = Modifier.then(
                    if(data.xData.size < 8){
                        Modifier.fillMaxWidth()
                    }else{
                        Modifier.width(50.dp * data.xData.size)
                    }
                ),
                title = {
                    Text(
                        data.title,
                        fontWeight = FontWeight.Bold
                    )
                },
                legend = { Legend(thumbnail, colorMap = colorMap, data = data) },
                legendLocation = LegendLocation.BOTTOM,
            ) {
                XYGraph(
                    xAxisModel = CategoryAxisModel(data.xData),
                    yAxisModel = FloatLinearAxisModel(
                        ceil(data.min - 5f).toFloat()..(ceil(data.max + 5f)).toFloat(),
                        minimumMajorTickSpacing = 50.dp,
                    ),
                    xAxisLabels = {
                        Text(it, Modifier.padding(top = 2.dp))
                    },
                    xAxisTitle = {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(data.xAxisTitle)
                        }
                    },
                    yAxisLabels = {
                        Text(it.toString(), Modifier.absolutePadding(right = 2.dp))
                    },
                    yAxisTitle = {
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Text(
                                data.yAxisTitle,
                                modifier = Modifier.rotateVertically(VerticalRotation.COUNTER_CLOCKWISE)
                                    .padding(bottom = 2.dp)
                            )
                        }
                    },
                    panZoomEnabled = false,

                    ) {
                    data.yMap.entries.sortedBy { it.key }.forEach { (city, rain) ->
                        chart(
                            city,
                            rain.mapIndexed { index, d ->
                                DefaultPoint(data.xData[index], d.toFloat())
                            },
                            thumbnail,
                            colorMap = colorMap
                        )
                    }
                }
            }
        }
    }else{
        Text("加载失败")
    }
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun XYGraphScope<String, Float>.chart(
    city: String,
    data: List<DefaultPoint<String, Float>>,
    thumbnail: Boolean,
    colorMap:Map<String,Color>
) {
    LinePlot(
        data = data,
        lineStyle = LineStyle(
            brush = SolidColor(colorMap[city] ?: Color.Black),
            strokeWidth = 2.dp
        ),
        symbol = { point ->
            Symbol(
                shape = CircleShape,
                fillBrush = SolidColor(colorMap[city] ?: Color.Black),
                modifier = Modifier.then(
                    if (!thumbnail) {
                        Modifier.hoverableElement {
//                            HoverSurface { Text(point.y.toString()) }
                        }
                    } else {
                        Modifier
                    }
                )
            )
        }
    )
}

@OptIn(ExperimentalKoalaPlotApi::class)
@Composable
private fun Legend(
    thumbnail: Boolean = false,
    data: LineChartData,
    colorMap :Map<String,Color>
) {
    val cities = data.yMap.keys.sorted()
    if (!thumbnail) {
        Surface{
            FlowLegend(
                itemCount = cities.size,
                symbol = { i ->
                    Symbol(
                        modifier = Modifier,
                        fillBrush = SolidColor(colorMap[cities[i]] ?: Color.Black)
                    )
                },
                label = { i ->
                    Text(cities[i])
                },
            )
        }
    }
}

@Serializable
class LineChartData(
    val xData  : List<String>,
    val yMap: Map<String,List<Float>>,
    val title :String,
    val xAxisTitle :String,
    val yAxisTitle :String,
){
    fun isAvailable():Boolean = xData.size == yMap.map {
        it.value.size
    }.max() && xData.size == yMap.map {
        it.value.size
    }.min() && xData.isNotEmpty() && yMap.isNotEmpty()
    val max = yMap.maxOf { it.value.maxOf { it } }
    val min = yMap.minOf { it.value.minOf { it } }

    fun createColorMap():Map<String,Color>{
        val colors = generateHueColorPalette(xData.size)
        return buildMap{
            yMap.keys.toList().fastForEachIndexed { i, item ->
                put(item, colors[i])
            }
        }
    }
}