package ui.compose.Manage

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import config.BaseUrlConfig
import data.share.Comment
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import util.compose.Label
import util.compose.shimmerLoadingAnimation
import util.network.NetworkResult
import util.network.toEasyTime


/**
 * 评论展示页面
 * @param comment Comment
 */
@Composable
fun CommentInReportDetail(
    comment: Comment,
) {
    Row(modifier = Modifier
        .padding(bottom = 10.dp)
        .animateContentSize()
        .clip(RoundedCornerShape(3.dp))
        .padding(vertical = 5.dp)
    ){
        //头像
        KamelImage(
            resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${comment.User.avatar}"),
            null,
            modifier = Modifier
                .size(50.dp)
                .wrapContentSize(Alignment.TopCenter)
                .fillMaxSize(0.7f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(10)),
            contentScale = ContentScale.FillBounds,
            onLoading = {

                Box(modifier = Modifier
                    .matchParentSize()
                    .shimmerLoadingAnimation()
                )
            }
        )
        Column (
            modifier = Modifier
                .padding(end = 10.dp)
                .weight(1f)
                .wrapContentHeight()
        ){
            Text(comment.User.username)
            Text(
                comment.Time.toEasyTime(),
                fontSize = 10.sp
            )
            Text(
                comment.Content,
                fontSize = 14.sp
            )
            comment.Image.let {
                if (it!=""){
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
                            Box(modifier = Modifier
                                .matchParentSize()
                                .shimmerLoadingAnimation()
                            )
                        }
                    )
                }
            }
            if( comment.Status == 1 ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(10))
                        .padding(vertical = 10.dp)
                ){
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize(),
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 10.sp,
                        text = "该评论遭到较多举报，请谨慎看待",
                        color = Color.Red
                    )
                }
            }
        }
    }
}


/**
 * 处理评论界面
 *
 * @constructor Create empty Manage comment voyager screen
 */
object ManageCommentVoyagerScreen:Screen{
    @OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinInject<ManageViewModel>()
        val commentReportPageList = viewModel.commentReportPageList.collectAsLazyPagingItems()
        val horizontalPage = rememberPagerState { commentReportPageList.itemCount }
        HorizontalPager(
            state = horizontalPage
        ){pageIndex ->
            commentReportPageList[pageIndex]?.let { commentReportData ->
                Column {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .weight(1f)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        CommentInReportDetail(
                            comment = commentReportData.comment,
                        )
                    }
                    FlowRow(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        Label(
                            "#版权问题:${commentReportData.commentReportContextData.CopyrightIssue}"
                        )
                        Label(
                            "#不当内容:${commentReportData.commentReportContextData.InappropriateContent}"
                        )
                        Label(
                            "#政治敏感:${commentReportData.commentReportContextData.PoliticallySensitive}"
                        )
                        Label(
                            "#信息滥用:${commentReportData.commentReportContextData.SpamAndAbuse}"
                        )
                        Label(
                            "#未经授权的广告:${commentReportData.commentReportContextData.UnauthorizedAdvertisement}"
                        )
                        Label(
                            "#隐私问题:${commentReportData.commentReportContextData.PrivacyIssue}"
                        )
                        Label(
                            "#违反社区准则:${commentReportData.commentReportContextData.ViolateCommunityGuidelines}"
                        )
                        Label(
                            "#恶意行为:${commentReportData.commentReportContextData.MaliciousBehavior}"
                        )
                    }
                    Crossfade(commentReportData.state.collectAsState().value){state ->
                        when(state){
                            is NetworkResult.UnSend -> {
                                Row(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth(1f)
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        modifier = Modifier,
                                        onClick = {
                                            viewModel.dealComment(commentReportData.state,commentReportData.comment.Id,commentReportData.comment.PostId,CommentProcessResult.BanComment)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = MaterialTheme.colors.surface,
                                            backgroundColor = MaterialTheme.colors.error
                                        )
                                    ) {
                                        Text("封禁")
                                    }
                                    Spacer(modifier = Modifier.width(100.dp))
                                    Button(
                                        modifier = Modifier,
                                        onClick = {
                                            viewModel.dealComment(commentReportData.state,commentReportData.comment.Id,commentReportData.comment.PostId,CommentProcessResult.PassComment)
                                        }
                                    ) {
                                        Text("举报无效")
                                    }
                                }
                            }
                            is NetworkResult.Error -> {
                                Row(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth(1f)
                                        .padding(vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Button(
                                        modifier = Modifier,
                                        onClick = {
                                            viewModel.dealComment(commentReportData.state,commentReportData.comment.Id,commentReportData.comment.PostId,CommentProcessResult.PassComment)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            contentColor = MaterialTheme.colors.surface,
                                            backgroundColor = MaterialTheme.colors.error
                                        )
                                    ) {
                                        Text("封禁")
                                    }
                                    Spacer(modifier = Modifier.width(100.dp))
                                    Button(
                                        modifier = Modifier,
                                        onClick = {
                                            viewModel.dealComment(commentReportData.state,commentReportData.comment.Id,commentReportData.comment.PostId,CommentProcessResult.BanComment)
                                        }
                                    ) {
                                        Text("举报无效")
                                    }
                                }
                            }
                            else -> {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center,
                                    modifier = Modifier
                                        .padding(bottom = 30.dp)
                                        .fillMaxWidth()
                                        .height(75.dp)
                                ){
                                    val scope = rememberCoroutineScope()
                                    Button(
                                        onClick = {
                                            scope.launch {
                                                horizontalPage.animateScrollToPage(
                                                    horizontalPage.currentPage+1
                                                )
                                            }

                                        }
                                    ){
                                        Icon(
                                            modifier = Modifier
                                                .fillMaxHeight(0.5f)
                                                .aspectRatio(1f),
                                            imageVector = Icons.Filled.Done,
                                            contentDescription = "",
                                            tint = Color.Green
                                        )
                                        Text(
                                            modifier = Modifier,
                                            text = "下一项"
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
}
