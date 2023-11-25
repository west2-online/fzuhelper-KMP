package ui.compose.Feedback

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cash.paging.compose.LazyPagingItems
import config.BaseUrlConfig.UserAvatar
import data.Feedback.FeedbackList.Data
import data.Feedback.FeedbackList.User
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.util.compose.Label
import ui.util.compose.ThemeCard
import ui.util.network.toEasyTime

@Composable
fun FeedbackList(
    modifier: Modifier,
    navigateToDetail: (id: Int) -> Unit,
    navigateToPost: () -> Unit,
    feedbackListFlow: LazyPagingItems<Data>
) {
    Box(modifier = Modifier){
        LazyColumn (
            modifier = modifier
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
