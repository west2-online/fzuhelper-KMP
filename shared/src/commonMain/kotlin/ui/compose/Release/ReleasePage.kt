package ui.compose.Release

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReleasePageScreen(
    modifier: Modifier = Modifier,
){
    val releasePageItems = remember {
        mutableStateListOf<ReleasePageItem>()
    }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    Column (
        modifier = modifier
    ){
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .weight(11f)
                .padding(top = 10.dp),
            state = lazyListState
        ){
            items(releasePageItems.size){ it ->
                Card (
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(5),
                ){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                    ){
                        when(releasePageItems.toList().sortedBy {
                            it.order
                        }[it]){
                            is ReleasePageItem.TextItem ->{
                                ReleasePageItemText(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .animateItemPlacement()
                                ) {
                                    scope.launch{
                                        lazyListState.animateScrollBy(it.toFloat())
                                    }
                                }
                            }
                            is ReleasePageItem.ImageItem ->{
                                ReleasePageItemImage(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .animateContentSize()
                                        .animateItemPlacement()
                                ) {

                                }
                            }
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.fillMaxWidth().height(250.dp))
            }
        }
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ){
            LazyRow(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                item{
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .clip(RoundedCornerShape(10))
                            .clickable {
                                releasePageItems.add(ReleasePageItem.TextItem(releasePageItems.size - 1))
                                scope.launch{
                                    lazyListState.animateScrollToItem(releasePageItems.size - 1)
                                }
                            }
                            .fillMaxSize(0.7f)
                        ,
                        imageVector = Icons.Filled.Build,
                        contentDescription = null
                    )
                }
                item{
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .clip(RoundedCornerShape(10))
                            .clickable {
                                scope.launch{
                                    releasePageItems.add(ReleasePageItem.ImageItem(releasePageItems.size - 1))
                                    lazyListState.animateScrollToItem(releasePageItems.size - 1)
                                }
                            }
                            .fillMaxSize(0.7f)
                        ,
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null
                    )
                }
            }
            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ){
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.AccountBox,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
            FloatingActionButton(
                onClick = {

                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ){
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Done,
                    contentDescription = null,
                    tint = Color.Green
                )
            }
        }

    }
}

interface ReleasePageItem{
    val order:Int
    data class TextItem(
        override val order: Int
    ) : ReleasePageItem
    data class ImageItem(
        override val order: Int
    ) : ReleasePageItem
}




@Composable
fun ReleasePageItemText(
    modifier: Modifier,
    onValueChange:(String)->Unit = {},
    overflow:(Int)->Unit = {},
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {}
){
    var text by rememberSaveable{
        mutableStateOf("")
    }
    LaunchedEffect(text){
        onValueChange(text)
    }
    Column( modifier ) {
        LazyRow(
            modifier = Modifier
                .height(60.dp)
                .padding(vertical = 5.dp)
        ) {
            item{
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            delete.invoke()
                        }
                        .padding(3.dp)
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
            item{
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            delete.invoke()
                        }
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Info,
                    contentDescription = null
                )
            }
            item{
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            delete.invoke()
                        }
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = {
                TextField(
                    value = text,
                    onValueChange = {
                        text = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize(
                            finishedListener = { init,target ->
                                println(target.height - init.height)
                                overflow.invoke(target.height - init.height)
                            }
                        )
                )
            }
        )
    }
}


@Composable
fun ReleasePageItemImage(
    modifier: Modifier,
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {}
){
    Column( modifier ) {
        LazyRow(
            modifier = Modifier
                .height(60.dp)
                .padding(vertical = 5.dp)
        ) {
            item{
                Button(
                    {}
                ){
                    Text("选择图片")
                }
            }
            item{
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            delete.invoke()
                        }
                        .padding(3.dp)
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
            item{
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            delete.invoke()
                        }
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Info,
                    contentDescription = null
                )
            }
            item{
                Icon(
                    modifier = Modifier
                        .fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .clip(RoundedCornerShape(10))
                        .clickable {
                            delete.invoke()
                        }
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.Delete,
                    contentDescription = null
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = {
                KamelImage(
                    resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
                    null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.Crop
                )
            }
        )
    }
}