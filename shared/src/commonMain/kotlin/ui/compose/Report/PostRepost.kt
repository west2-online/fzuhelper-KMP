package ui.compose.Report

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import config.BaseUrlConfig
import data.share.Post
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import ui.compose.Post.PersonalInformationAreaInList
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import kotlin.jvm.Transient

/**
 * 帖子举报界面
 * @param modifier Modifier
 * @param data Post
 */
@Composable
fun PostReport(
    modifier: Modifier = Modifier,
    data : Post
){
    val viewModel = koinInject<ReportViewModel>()
    val selectItem = remember {
        mutableStateOf(0)
    }
    val reportResponseState = viewModel.reportPostResponse.collectAsState()
    val toastState = rememberToastState()
    toastState.toastBindNetworkResult(reportResponseState)
    LazyColumn(
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ){
        item{
            Column(
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Text("举报@", color = Color.Red,modifier = Modifier.padding(bottom = 10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                        .clip(RoundedCornerShape(5.dp))
                        .padding(10.dp)
                ){
                    PersonalInformationAreaInList(
                        userAvatar = data.User.avatar ,
                        userName = data.User.username ,
                    )
                    data.FirstImage?.let {
                        if(it.isEmpty()){
                            return@let
                        }
                        KamelImage(
                            resource = asyncPainterResource("${BaseUrlConfig.PostImage}/${it}"),
                            null,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .fillMaxWidth(),
                            contentScale = ContentScale.Fit,
                            onFailure = {
                                Text("加载失败")
                            },
                            onLoading = {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(vertical = 20.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        )
                    }

                    Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(end = 10.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        text = data.Title
                    )
                    Text(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize(),
                        maxLines = 10,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp,
                        text = data.LittleDescribe?:""
                    )
                }
            }
        }
        ReportLabel.values().forEachIndexed { index, reportLabel ->
            item {
                Box(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(5.dp))
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                        .background(
                            animateColorAsState(if (index == selectItem.value) MaterialTheme.colors.error else MaterialTheme.colors.surface).value
                        )
                        .clickable {
                            selectItem.value = index
                        }
                        .padding(5.dp)
                ) {
                    Column {
                        Text(reportLabel.reason)
                        Text(reportLabel.description, fontSize = 10.sp)
                    }
                }
            }
        }
        item {
            Button(
                modifier = Modifier
                    .fillMaxWidth(0.4f),
                onClick = {
                    viewModel.reportPost(selectItem.value, data.Id.toString())
                }
            ) {
                Text("举报")
            }
        }
    }
    EasyToast(toastState)
}

/**
 * 帖子举报界面 二级界面
 * @property type PostReportType
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class PostRepostVoyagerScreen(
    @Transient
    val type : ReportType.PostReportType,
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
): Screen {
    @Composable
    override fun Content() {
        PostReport(
            data = type.data,
            modifier = Modifier
                .parentSystemControl(parentPaddingControl)
        )
    }
}

