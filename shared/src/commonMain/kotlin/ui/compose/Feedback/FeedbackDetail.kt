package ui.compose.Feedback

import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import config.BaseUrlConfig
import data.feedback.FeelbackDetail.Data
import data.feedback.FeelbackDetail.FeedbackComment
import data.feedback.FeelbackDetail.FeedbackStatu
import data.share.User
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import org.example.library.MR
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.Toast
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.network.CollectWithContentInBox
import util.network.NetworkResult
import util.network.toEasyTime
import util.network.toast
import kotlin.jvm.Transient

const val SpaceWeight = 0.2f
@Composable
fun FeedbackDetail(
    modifier: Modifier,
    detailState: State<NetworkResult<Data>>,
    getDetailData: () -> Unit,
    toastState: Toast,
    postNewComment: (content: String) -> Unit,
    commentState: State<NetworkResult<String>>
){
    LaunchedEffect(Unit){
        getDetailData()
    }
    LaunchedEffect(commentState.value.key.value){
        commentState.value.toast(
            success = {
                toastState.addWarnToast("评论成功")
            },
            error = {
                toastState.addWarnToast("评论失败")
            }
        )
    }
    val comment = remember {
        mutableStateOf("")
    }
    detailState.CollectWithContentInBox(
        success = {
            Box(
                modifier = Modifier
            ){
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val isRefresh = remember{
                        mutableStateOf(false)
                    }
                    val state = rememberLazyListState()
                    LaunchedEffect(state){
                        snapshotFlow{ state.isScrollInProgress && !state.canScrollBackward }
                            .filter {
                                it
                            }
                            .collect {
                                isRefresh.value = true
                                delay(1000)
                                getDetailData.invoke()
                                isRefresh.value = false
                            }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ){
                        //画中间线
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Spacer(
                                modifier = Modifier
                                    .weight(SpaceWeight)
                            )
                            Canvas(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(50.dp)
                            ) {
                                drawLine(
                                    Color.Black,
                                    start = Offset(center.x, 0f),
                                    end = Offset(center.x, 2 * center.y)
                                )
                            }
                            Spacer(
                                modifier = Modifier
                                    .weight(1f)
                            )
                        }
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            state = state
                        ) {
                            item {
                                Numbering(it.Feedback.Id)
                            }
                            item {
                                Discuss(
                                    content = it.Feedback.Tab,
                                    time = it.Feedback.Time,
                                    identity = "",
                                    user = it.Feedback.User
                                )
                            }
                            it.FeedbackComment.plus(it.FeedbackStatus).sortedBy { it.Order }
                                .forEach {
                                    when (it) {
                                        is FeedbackComment -> {
                                            item {
                                                Discuss(
                                                    content = it.Comment,
                                                    time = it.Time,
                                                    identity = "",
                                                    user = it.User
                                                )
                                            }
                                        }

                                        is FeedbackStatu -> {
                                            item {
                                                StateLabel(
                                                    type = LabelType.Closed,
                                                    commit = it.Message,
                                                    time = it.Time.toEasyTime()
                                                )
                                            }
                                        }
                                    }
                                }
                        }
                        androidx.compose.animation.AnimatedVisibility(
                            isRefresh.value,
                            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
                            exit = slideOutVertically(),
                            enter = slideInVertically()
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(5.dp)
                                        .align(Alignment.Center)
                                        .size(30.dp)
                                )
                            }

                        }
                    }
                    TextField(
                        value = comment.value,
                        onValueChange = {
                            comment.value = it
                        },
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .padding(bottom = 5.dp)
                            .padding(horizontal = 10.dp)
                            .wrapContentHeight()
                            .fillMaxWidth(),
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = null,
                                tint = Color.Green,
                                modifier = Modifier
                                    .size(40.dp)
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.8f)
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .clickable {
                                        postNewComment(comment.value)
                                    }
                            )
                        }
                    )
                }
            }

        },
        error = {
            Text("加载失败")
        },
        modifier = modifier
    )
}

@Composable
fun StateLabel(
    time :String = "",
    type : LabelType,
    commit : String = "TestTestTestTesTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTesttTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest"
){
    Row(
        modifier = Modifier
            .padding(vertical = 10.dp)
            .fillMaxWidth()
            .wrapContentHeight()
    ){
        Spacer(
            modifier = Modifier
                .weight(SpaceWeight)
        )
        Icon(
            painter = painterResource(type.icon),
            "",
            modifier = Modifier
                .size(50.dp)
                .wrapContentSize(Alignment.TopCenter)
                .fillMaxSize(0.7f)
                .clip(CircleShape)
                .background(type.background)
                .wrapContentSize(Alignment.Center)
                .fillMaxSize(0.7f)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
        ) {
            Text(
                time,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(bottom = 3.dp)
            )
            Text(
                commit,
                fontSize = 10.sp
            )
        }

    }
}

enum class LabelType(val background: Color, val icon: ImageResource, val value: Int, val description: String) {
    ActiveStatus(Color.Green, icon = MR.images.activeStatus, 0, "活跃状态"),
    Closed(Color.Red, icon = MR.images.close, 1, "已关闭"),
    ItCanTBeSolvedForTheTimeBeing(Color.Yellow, icon = MR.images.not_solved, 2, "目前无法解决"),
    Postpone(Color(155, 114, 211), icon = MR.images.time_delay, 3, "延期"),
    Resolved(Color.Green, icon = MR.images.done, 4, "已解决"),
}

fun Int.toLabelType():LabelType{
    return LabelType.values().find {
        it.value == this
    }?:LabelType.ActiveStatus
}

@Composable
fun Discuss(
    content:String,
    time:String,
    identity :String,
    user: User
){
    Card (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentSize(),
        shape = RoundedCornerShape(10.dp)
    ){
        Row(Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp)){
            KamelImage(
                resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${user.avatar}"),
                null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(50.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )
            Column (
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ){
                Text(
                    time,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
//                Label("开发者")
                Text(
                    content,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
fun Numbering(
    id:Int
){
    Card (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp)
    ){
        Text(
            "#${id}",
            modifier = Modifier
                .padding(10.dp),
        )
    }
}

class FeedbackDetailVoyagerScreen(
    private val feedbackId : Int,
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
):Screen{
    @Composable
    override fun Content() {
        val feedBackViewModel: FeedBackViewModel = koinInject()
        val toastState = rememberToastState()
        FeedbackDetail(
            modifier = Modifier
                .fillMaxSize()
                .parentSystemControl(parentPaddingControl),
            getDetailData = {
                feedBackViewModel.getFeedbackDetail(feedbackId)
            },
            toastState = toastState,
            postNewComment = { content ->
                feedBackViewModel.postFeedbackDetailComment(content = content, id = feedbackId)
            },
            commentState = feedBackViewModel.commentResult.collectAsState(),
            detailState = feedBackViewModel.detailResult.collectAsState(),
        )
        EasyToast(toastState)
    }
}