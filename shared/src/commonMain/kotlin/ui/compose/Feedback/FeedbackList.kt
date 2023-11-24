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
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.util.compose.Label
import ui.util.compose.ThemeCard

@Composable
fun FeedbackList(
    modifier: Modifier,
    navigateToDetail:(id:String)->Unit
) {
    Box(modifier = Modifier){
        LazyColumn (
            modifier = modifier
        ){
            items(10){
                FeedbackListItem(
                    navigateToDetail = navigateToDetail
                )
            }
        }
        FloatingActionButton(
            onClick = {

            },
            modifier = Modifier
                .offset(x = (-15).dp,y = ((-5).dp))
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
    navigateToDetail:(id:String)->Unit
) {
    ThemeCard(
        cardModifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable {
                navigateToDetail("")
            }
            .padding(10.dp),
        columnModifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .padding(10.dp),
        shape = RoundedCornerShape(10.dp)
    ){
        Text(
            "#123",
            modifier = Modifier,
        )
        DiscussInList(
            userId = "", content = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest", time = "2023.1.10", identity = "开发者"
        )
    }
}

@Composable
fun DiscussInList(
    userId:String ,
    content:String,
    time:String,
    identity :String,
    type: LabelType = LabelType.Down
){
    Column{
        Row(
            Modifier.fillMaxWidth().wrapContentHeight().padding(top = 10.dp)
        ) {
            KamelImage(
                resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
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
                imageVector = type.icon,
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
            Text(type.name)
        }
    }
}
