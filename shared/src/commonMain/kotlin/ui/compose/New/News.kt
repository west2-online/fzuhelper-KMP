package ui.compose.New

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.util.Label


@Composable
fun NewsScreen(
    modifier: Modifier = Modifier
){
    LazyColumn(
        modifier = modifier,
    ){
        items(10){
            NewsItem()
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsItem(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .wrapContentHeight()
        .animateContentSize()
){
    var isUnfold by rememberSaveable {
        mutableStateOf(false)
    }
    val lines by animateIntAsState(
        if(isUnfold) 10 else 4
    )
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier,
        elevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {

                }
                .padding(10.dp)
        ) {
            PersonalInformationArea()
            Surface (
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ){
                FlowRow(
                    modifier = Modifier
                        .fillMaxSize(),
                ) {
                    repeat(30) {
                        Label("#"+"s"*((1..10).random()))
                    }
                }
            }
            KamelImage(
                resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
                null,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .aspectRatio(2f),
                contentScale = ContentScale.FillBounds
            )
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
                text = "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试试测试测试测试测试"
            )
            Text(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
                maxLines = lines,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                text = "测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试测试试测试测试测试测试"
            )
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    modifier = Modifier.weight(1f),
                    text = "2023.0.01",
                    fontSize = 10.sp
                )
                Button(
                    onClick = {
                        isUnfold = !isUnfold
                    }
                ){
                    Text(
                        text = if (isUnfold) "收起" else "展开"
                    )
                }
            }
            Interaction(modifier = Modifier.fillMaxWidth(0.5f).wrapContentHeight())
        }

    }
}

private operator fun String.times(int: Int): String {
    var data = ""
    for (i in 0 until int) {
        data += this
    }
    return data
}

@Composable
fun PersonalInformationArea(
    url : String = "https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg",
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(50.dp),
    userName : String = "theonenull",
){
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        KamelImage(
            resource = asyncPainterResource(url),
            null,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f)
                .wrapContentSize(Alignment.CenterStart)
                .fillMaxSize(0.7f)
                .clip(RoundedCornerShape(10)),
            contentScale = ContentScale.FillBounds
        )
        Text(
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
                .wrapContentHeight(),
            text = userName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
fun Interaction(
    modifier: Modifier
){
    BottomNavigation(
        contentColor = Color.Transparent,
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        modifier = modifier
    ){
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
            label = { Text("100") },
            selected = false,
            onClick = {  },
            modifier = Modifier
                .clip(CircleShape)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = null) },
            label = { Text("100") },
            selected = false,
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Share, contentDescription = null) },
            label = { Text("100") },
            selected = false,
            onClick = { },
            modifier = Modifier
                .clip(CircleShape)
        )
    }
}



enum class NewsLabel(name:String) {
    JAVA("java"),
    KOTLIN("kotlin"),
    PYTHON("python"),
    C("c"),
    CPP("cpp"),
    OTHER("other"),
}

