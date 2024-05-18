package ui.compose.Release

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ui.compose.PostDisplayShare.ReleasePageItemImageForShow
import ui.compose.PostDisplayShare.ReleasePageItemLineChartForShow
import ui.compose.PostDisplayShare.ReleasePageItemTextForShow
import util.compose.Label

/**
 * 发布帖子预览界面
 * @param lazyListState LazyListState
 * @param title MutableState<String>
 * @param releasePageItems SnapshotStateList<ReleasePageItem>
 * @param labelList List<LabelForSelect>
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun PreviewContent(
    lazyListState: LazyListState,
    title: MutableState<String>,
    releasePageItems: SnapshotStateList<ReleasePageItem>,
    labelList: List<LabelForSelect>
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
                            ReleasePageItemLineChartForShow(
                                releasePageItem
                            )
                        }
                    }
                }
            }
        }
    }
}