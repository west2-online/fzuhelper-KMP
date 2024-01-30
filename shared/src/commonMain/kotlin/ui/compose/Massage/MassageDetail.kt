package ui.compose.Massage

import BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip

@Composable
fun MassageDetail(
    modifier: Modifier = Modifier,
    back:(()->Unit)? = null
){
    BackHandler(back!=null){
        back?.invoke()
    }
    val listState = rememberLazyListState()
    val isShowTopBar = remember {
        mutableStateOf(true)
    }
    val first  = remember {
        mutableStateOf(Pair(1,0))
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex  }
            .zip(snapshotFlow { listState.firstVisibleItemScrollOffset }){ index,offset->
                Pair(index,offset)
            }
            .map {
                it
            }.collect {
                val used = first.value
                if(used!=it){
                    first.value = it
                }
                isShowTopBar.value = used.first > it.first || (used.first == it.first && used.second > it.second)
            }
    }
    Box(modifier){
        Column {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                state = listState,
            ) {
                item {
                    Box(modifier = Modifier.padding(10.dp).wrapContentHeight().width(0.dp).padding(10.dp)){
                        Text("")
                    }
                }
                items(30) {
                    MassageDetailItem(
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .animateContentSize()
                    ) {
                        TextWithLink()
                    }
                }
            }
            TextField(
                value = "",
                onValueChange = {},
                label = {
                    Text("回复")
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth()
                    .height(56.dp)
            )
        }
        AnimatedVisibility(
            visible = isShowTopBar.value,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colors.primarySurface)
                    .padding(10.dp)
            ) {
                Text(
                    "FuTALK",
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }
        }
    }
}

@Composable
fun MassageDetailItem(
    modifier: Modifier = Modifier,
    itemContent : @Composable BoxScope.() -> Unit = {}
){
    Row (
        modifier = modifier
    ) {
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
            null,
            modifier = Modifier
                .height(50.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
            contentScale = ContentScale.FillBounds
        )
        Box (
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 10.dp)
                .wrapContentHeight()
                .clip(RoundedCornerShape(0.dp,10.dp,0.dp,10.dp))
                .background(Color(83, 198, 236))
                .padding(10.dp)
        ){
            itemContent()
        }
    }
}

@Composable
fun TextWithLink(
    modifier: Modifier = Modifier
){
    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.End
    ) {
        Text("sssssssssssssssssssssssssssssssssssssssssssssssss")
        FloatingActionButton(
            onClick = {},
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .wrapContentHeight(),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                "https://github.com/",
                modifier = Modifier
                    .padding(10.dp)
            )
        }
        Text(
            "2023.10.1",
            modifier = Modifier.padding( top = 10.dp ),
            fontSize = 10.sp
        )
        Text(
            "In FuZhou",
            modifier = Modifier.padding( ),
            fontSize = 10.sp
        )
    }
}