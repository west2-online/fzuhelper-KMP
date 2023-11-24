package ui.compose.Feedback

import BackHandler
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.util.compose.Label

const val SpaceWeight = 0.2f
@Composable
fun FeedbackDetail(
    modifier: Modifier,
    back: (() -> Unit)?
){
    val comment = remember {
        mutableStateOf("")
    }
    BackHandler(back!=null){
        back?.invoke()
    }
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            Row(
                modifier = Modifier
                    .fillMaxSize()
            ) {
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
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Numbering()
                }
                item {
                    Discuss(
                        userId = "",
                        content = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest",
                        time = "2023.0.01",
                        identity = ""
                    )
                }
                items(10) {
                    StateLabel(
                        LabelType.Down
                    )
                }
                item {
                    Discuss(
                        userId = "",
                        content = "testtesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttesttest",
                        time = "2023.0.01",
                        identity = ""
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

                        }
                )
            }
        )
    }
}

@Composable
fun FeedbackDetailItem(){

}

@Composable
fun StateLabel(
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
            imageVector = type.icon,
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
        Text(
            commit,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight()
                .padding(horizontal = 10.dp),
            fontSize = 10.sp
        )
    }
}

enum class LabelType(val background:Color,val icon:ImageVector){
    Down(Color.Green, icon = Icons.Filled.Done)
}
@Composable
fun Discuss(
    userId:String ,
    content:String,
    time:String,
    identity :String
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
                resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
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
                Label("开发者")
                Text(
                    content,
                    modifier = Modifier

                )
            }
        }
    }
}

@Composable
fun Numbering(){
    Card (
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        shape = RoundedCornerShape(10.dp)
    ){
        Text(
            "#123",
            modifier = Modifier
                .padding(10.dp),
        )
    }
}