package ui.compose.Feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import config.BaseUrlConfig.UserAvatar
import data.feedback.FeedbackList.Data
import data.feedback.FeedbackList.User
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.Label
import util.compose.ThemeCard
import util.compose.Toast
import util.compose.rememberToastState
import util.network.toEasyTime

@Composable
fun FeedbackList(
    modifier: Modifier,
    navigateToDetail: (id: Int) -> Unit,
    navigateToPost: () -> Unit,
    feedbackListFlow: LazyPagingItems<Data>,
    toastState: Toast
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
                feedbackListFlow.refresh()
                isRefresh.value = false
            }
    }

    Box(modifier = Modifier){
        LazyColumn (
            modifier = modifier,
            state = state
        ){
            items(feedbackListFlow.itemCount){
                feedbackListFlow[it]?.let {
                    FeedbackListItem(
                        navigateToDetail = navigateToDetail,
                        feedback = it
                    )
                }
            }
        }
        FloatingActionButton(
            onClick = {
                navigateToPost.invoke()
            },
            modifier = Modifier
                .offset(x = (-15).dp,y = ((-15).dp))
                .size(50.dp)
                .align(Alignment.BottomEnd),
            shape = CircleShape
        ){
            Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .fillMaxSize(0.5f),
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
        }
        AnimatedVisibility(
            isRefresh.value,
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
            exit = slideOutVertically(),
            enter = slideInVertically()
        ){
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)){
                CircularProgressIndicator(
                    modifier = Modifier
                        .padding(5.dp)
                        .align(Alignment.Center)
                        .size(30.dp)
                )
            }

        }
    }
}

@Composable
fun FeedbackListItem(
    navigateToDetail: (id: Int) -> Unit,
    feedback: Data
) {
    ThemeCard(
        cardModifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                navigateToDetail(feedback.Id)
            }
            .padding(10.dp),
        columnModifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp)
    ){
        Row {
            Text(
                "#${feedback.Id} ${if(feedback.Type == 0) "反馈" else "Bug"}",
                modifier = Modifier,
            )
        }
        DiscussInList(
            user = feedback.User,
            content = feedback.Tab,
            time = feedback.Time.toEasyTime(),
            identity = "开发者",
            type = feedback.Status.toLabelType()
        )
    }
}

@Composable
fun DiscussInList(
    user:User ,
    content:String,
    time:String,
    identity :String,
    type: LabelType = LabelType.ActiveStatus
){
    Column{
        Row(
            Modifier.fillMaxWidth().wrapContentHeight().padding(top = 10.dp)
        ) {
            KamelImage(
                resource = asyncPainterResource("${UserAvatar}/${user.avatar}"),
                null,
                modifier = Modifier
                    .padding(end = 10.dp)
                    .width(50.dp)
                    .aspectRatio(1f)
                    .clip(CircleShape),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {
                Text(
                    time,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                )
                Label("开发者")
                Text(
                    content,
                    modifier = Modifier
                )
            }
        }
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(type.icon),
                "",
                modifier = Modifier
                    .size(50.dp)
                    .wrapContentSize(Alignment.Center)
                    .fillMaxSize(0.7f)
                    .clip(CircleShape)
                    .background(type.background)
                    .wrapContentSize(Alignment.Center)
                    .fillMaxSize(0.7f)
            )
            Text(type.description)
        }
    }
}

class FeedbackListVoyagerScreen(

) :Screen{
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: FeedBackViewModel = koinInject()
        val toastState = rememberToastState()
        FeedbackList(
            modifier = Modifier
                .fillMaxSize(),
            navigateToDetail = { postId ->
                navigator.push(FeedbackDetailVoyagerScreen(postId))
            },
            navigateToPost = {
                navigator.push(FeedbackPostVoyagerScreen())
            },
            toastState = toastState,
            feedbackListFlow = viewModel.postListFlow.collectAsLazyPagingItems()
        )
        EasyToast(toastState)
    }
}
