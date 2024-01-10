package ui.compose.Weather

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import data.weather.Forecast
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.util.network.CollectWithContent

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeatherScreen(
    viewModel: WeatherViewModel = koinInject()
){
    LaunchedEffect(Unit){
        viewModel.getFuZhouWeather()
    }
    val weatherDataOfFuZhou = viewModel.weatherDataOfFuZhou.collectAsState()
    val scope = rememberCoroutineScope()
    weatherDataOfFuZhou.CollectWithContent(
        success = { weatherData->
            val pageState = rememberPagerState {
                weatherData.data.forecast.size
            }
            val currentPage = animateFloatAsState(pageState.currentPage.toFloat())
            Column {
                PreviewForSevenDays(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(2.4f),
                    weatherDataList = weatherData.data.forecast,
                    current = currentPage.value,
                    click = {
                        scope.launch {
                            pageState.animateScrollToPage(it)
                        }
                    }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 10.dp)
                ){
                    Column(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                    ){
                        Text(" —- 最高温", color = Color.Red, fontSize = 10.sp)
                        Text(" —- 最低温", color = Color.Blue, fontSize = 10.sp)
                    }
                    HorizontalPager(
                        state = pageState
                    ) {
                        Column {
                            Text(text = "⏲\uFE0F日期: ${weatherData.data.forecast[it].date}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83C\uDF21\uFE0F最高温度: ${weatherData.data.forecast[it].high}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83C\uDF21\uFE0F最低温度: ${weatherData.data.forecast[it].low}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "⌛日期: ${weatherData.data.forecast[it].ymd}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "⭐星期: ${weatherData.data.forecast[it].week}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "☀\uFE0F日出时间: ${weatherData.data.forecast[it].sunrise}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83C\uDF1B日落: ${weatherData.data.forecast[it].sunset}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "☁\uFE0F空气质量: ${weatherData.data.forecast[it].aqi}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83C\uDF90风向: ${weatherData.data.forecast[it].fx}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83C\uDF43风力: ${weatherData.data.forecast[it].fl}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83C\uDF26\uFE0F天气: ${weatherData.data.forecast[it].type}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                            Text(text = "\uD83D\uDC4B 提醒: ${weatherData.data.forecast[it].notice}",
                                modifier = Modifier.padding(bottom = 6.dp).wrapContentHeight()
                                    .fillMaxWidth().padding(3.dp)
                            )
                        }
                    }
                }
            }

        },
        loading = {
            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
            ){
                CircularProgressIndicator()
                Text("加载中")
            }
        }
    )
}

@Composable
fun PreviewForSevenDays(
    weatherDataList: List<Forecast>,
    modifier: Modifier,
    click: (Int) -> Unit = {},
    current: Float = 0f
){
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(3.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(3.dp))
    ){
        Box(
            modifier = modifier
        ) {
            Row {
                weatherDataList.forEachIndexed { index,_ ->
                    Box(
                        modifier = Modifier.weight(1f)
                            .fillMaxHeight()
                            .clickable {
                                click.invoke(index)
                            }
                    )
                }
            }
            val textMeasure = rememberTextMeasurer()
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 10.dp)
            ) {
                val number = weatherDataList.size
                if (number == 0) {
                    return@Canvas
                }
                val temperatureRegex = Regex("""(-?\d+)℃""")
                val highList = weatherDataList.map {
                    val matchResultMax = temperatureRegex.find(it.high)
                    // 如果找到匹配项，则提取温度值
                    val temperatureMax = matchResultMax?.groups?.get(1)?.value?.toInt() ?: -1000
                    if (temperatureMax == -1000) {
                        return@Canvas
                    }
                    return@map temperatureMax
                }
                val lowList = weatherDataList.map {
                    val matchResultMax = temperatureRegex.find(it.low)
                    // 如果找到匹配项，则提取温度值
                    val temperatureMax = matchResultMax?.groups?.get(1)?.value?.toInt() ?: -1000
                    if (temperatureMax == -1000) {
                        return@Canvas
                    }
                    return@map temperatureMax
                }
                val maxOfHigh = highList.max().toFloat()
                val minOfHigh = highList.min().toFloat()
                highList.forEachIndexed { index, tem ->
                    if (index != number - 1) {
                        val end = highList[index + 1]
                        drawLine(
                            Color.Red,
                            start = Offset(
                                x = index * size.width / number + size.width / number / 2,
                                y = (1 - (tem - minOfHigh) / (maxOfHigh - minOfHigh)) * size.height
                            ),
                            end = Offset(
                                x = (index + 1) * size.width / number + size.width / number / 2,
                                y = (1 - (end - minOfHigh) / (maxOfHigh - minOfHigh)) * size.height
                            )
                        )
                    }
                }
                val maxOfLow = lowList.max().toFloat()
                val minOfLow = lowList.min().toFloat()
                lowList.forEachIndexed { index, tem ->
                    if (index != number - 1) {
                        val end = lowList[index + 1]
                        drawLine(
                            Color.Blue,
                            start = Offset(
                                x = index * size.width / number + size.width / number / 2,
                                y = (1 - (tem - minOfLow) / (maxOfLow - minOfLow)) * size.height
                            ),
                            end = Offset(
                                x = (index + 1) * size.width / number + size.width / number / 2,
                                y = (1 - (end - minOfLow) / (maxOfLow - minOfLow)) * size.height
                            )
                        )
                    }
                }
                val maxText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(maxOfHigh.toString())
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append("/")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(maxOfLow.toString())
                    }
                }
                drawText(
                    textLayoutResult = textMeasure.measure(maxText),
                    color = Color.Red,
                    topLeft = Offset.Zero
                )
                val minText = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(minOfHigh.toString())
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append("/")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.Blue,
                            fontSize = 10.sp,
                            fontStyle = FontStyle.Italic
                        )
                    ) {
                        append(minOfLow.toString())
                    }
                }
                val measure = textMeasure.measure(minText)
                drawText(
                    textLayoutResult = measure,
                    color = Color.Red,
                    topLeft = Offset.Zero.copy(y = size.height - measure.size.height)
                )
                drawLine(
                    Color.Blue,
                    start = Offset(
                        x = current * size.width / number + size.width / number / 2,
                        y = 0f
                    ),
                    end = Offset(
                        x = current * size.width / number + size.width / number / 2,
                        y = size.height
                    ), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )
            }
        }
        Row {
            weatherDataList.forEachIndexed { index,item ->
                Text(
                    modifier = Modifier.weight(1f)
                        .wrapContentHeight()
                        .background(
                            if( index == current.toInt() ) Color.Gray else Color.Transparent
                        ),
                    text = item.date,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}


class WeatherRouteNode(
    buildContext:BuildContext
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        WeatherScreen()
    }
}