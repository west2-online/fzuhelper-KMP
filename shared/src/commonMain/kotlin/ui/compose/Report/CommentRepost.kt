package ui.compose.Report

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import data.post.PostList.PostListItemData
import data.share.Comment
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import ui.compose.Post.PersonalInformationAreaInList
import util.compose.EasyToast
import util.compose.rememberToastState
import util.compose.shimmerLoadingAnimation
import util.compose.toastBindNetworkResult
import util.network.toEasyTime

@Composable
fun CommentRepost(
    modifier: Modifier = Modifier,
    commentData: Comment
){
    val viewModel = koinInject<ReportViewModel>()
    val selectItem = remember {
        mutableStateOf(0)
    }
    val reportResponseState = viewModel.reportCommentResponse.collectAsState()
    val toastState = rememberToastState()
    toastState.toastBindNetworkResult(reportResponseState)
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ){
        item{
            Column(
                modifier = Modifier
            ) {
                Text("举报@", color = Color.Red, modifier = Modifier.padding(bottom = 10.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                        .clip(RoundedCornerShape(5.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .animateContentSize()
                            .clip(RoundedCornerShape(3.dp))
                            .padding(vertical = 5.dp)
                    ) {
                        KamelImage(
                            resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${commentData.User.avatar}"),
                            null,
                            modifier = Modifier
                                .size(50.dp)
                                .wrapContentSize(Alignment.TopCenter)
                                .fillMaxSize(0.7f)
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10)),
                            contentScale = ContentScale.FillBounds,
                            onLoading = {

                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .shimmerLoadingAnimation()
                                )
                            }
                        )
                        Column(
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .weight(1f)
                                .wrapContentHeight()
                        ) {
                            Text(commentData.User.username)
                            Text(
                                commentData.Time.toEasyTime(),
                                fontSize = 10.sp
                            )
                            Text(
                                commentData.Content,
                                fontSize = 14.sp
                            )
                            commentData.Image.let {
                                if(it.isEmpty()){
                                    return@let
                                }
                                KamelImage(
                                    resource = asyncPainterResource("${BaseUrlConfig.CommentImage}/${it}"),
                                    null,
                                    modifier = Modifier
                                        .padding(vertical = 5.dp)
                                        .fillMaxWidth(0.5f)
                                        .wrapContentHeight()
                                        .clip(RoundedCornerShape(3))
                                        .animateContentSize(),
                                    contentScale = ContentScale.Inside,
                                    onLoading = {
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .shimmerLoadingAnimation()
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
        ReportLabel.values().forEachIndexed { index, reportLabel ->
            item {
                Box(
                    modifier = Modifier
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
            Row (
                horizontalArrangement = Arrangement.End
            ){
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.4f),
                    onClick = {
                        viewModel.reportComment(commentId = commentData.Id.toString(), selectItem.value, postId = commentData.PostId.toString())
                    }
                ) {
                    Text("举报")
                }
            }
        }
    }
    EasyToast(toastState)
}

@Composable
fun PostReportInCommentReport(
    modifier: Modifier = Modifier,
    data : PostListItemData
){
    val post = data.Post
    Column(
        modifier = modifier
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
                userAvatar = post.User.avatar ,
                userName = post.User.username ,
            )
            post.FirstImage?.let {
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
                text = post.Title
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
                text = post.LittleDescribe?:""
            )
        }
    }
}

class CommentReportVoyagerScreen(
    val type : ReportType.CommentReportType
): Screen {
    @Composable
    override fun Content() {
        CommentRepost(
            commentData = type.comment
        )
    }
}