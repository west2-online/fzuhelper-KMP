package ui.compose.Release

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import util.compose.Label

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun PreviewContent(
    lazyListState: LazyListState,
    title: MutableState<String>,
    releasePageItems: SnapshotStateList<ReleasePageItem>,
    labelList: SnapshotStateList<LabelForSelect>
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    content = {
                        Text(
                            text = title.value,
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp),
                            fontSize = 20.sp
                        )
                    }
                )
            }
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
            ) {
                labelList.filter {
                    it.isSelect.value
                }.forEach { label ->
                    Label(label.label)
                }
            }
        }
        releasePageItems.toList().filter {
            return@filter when (it) {
                is ReleasePageItem.TextItem -> it.text.value != ""
                is ReleasePageItem.ImageItem -> it.image.value != null
                is ReleasePageItem.LineChartItem -> it.lineParameters.isNotEmpty()
                else -> false
            }
        }.forEachIndexed { _, releasePageItem ->
            item {
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp)
                ) {
                    when (releasePageItem) {
                        is ReleasePageItem.TextItem -> {
                            ReleasePageItemTextForShow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .animateItemPlacement(),
                                text = releasePageItem.text,
                            )
                        }

                        is ReleasePageItem.ImageItem -> {
                            ReleasePageItemImageForShow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .animateContentSize()
                                    .animateItemPlacement(),
                                image = releasePageItem.image
                            )
                        }

                        is ReleasePageItem.LineChartItem -> {
                            Box(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                            ){
                                Box(
                                    modifier = Modifier
                                        .height(300.dp)
                                        .width((50.dp + (100 * releasePageItem.lineParameters.size).dp))
                                ){
                                    LineChart(
                                        modifier = Modifier.fillMaxSize(),
                                        linesParameters = listOf(
                                            LineParameters(
                                                label = releasePageItem.label.value,
                                                data = releasePageItem.lineParameters.map {
                                                    it.y.value.toDouble()
                                                },
                                                lineColor = releasePageItem.gridColor.value,
                                                lineType = releasePageItem.lineType.value,
                                                lineShadow = releasePageItem.lineShadow.value,
                                            )
                                        ),
                                        isGrid = releasePageItem.isGrid.value,
                                        gridColor = Color.Blue,
                                        xAxisData = releasePageItem.lineParameters.map {
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
            }
        }
    }
}

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
