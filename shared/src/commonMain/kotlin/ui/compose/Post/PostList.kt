package ui.compose.Post

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import config.BaseUrlConfig
import config.BaseUrlConfig.PostImage
import data.post.PostList.Data
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import ui.util.network.toEasyTime


@Composable
fun NewsList(
    modifier: Modifier = Modifier,
    navigateToNewsDetail: (String) -> Unit,
//    postListState: State<NetworkResult<PostList>>,
    state: LazyListState,
    postListFlow: LazyPagingItems<Data>,
    navigateToRelease: () -> Unit
){
    val isRefresh = remember{
        mutableStateOf(false)
    }
    LaunchedEffect(state){
        snapshotFlow{ state.isScrollInProgress && !state.canScrollBackward }
            .filter {
                it
            }
            .collect {
                isRefresh.value = true
                delay(1000)
                postListFlow.refresh()
                isRefresh.value = false
            }
    }

    Box(modifier = modifier){
//        postListState.let { networkResultState ->
//            networkResultState.CollectWithContent(
//                success = { list ->
//                    list.data?.let{ postList ->
//                        LazyColumn(
//                            modifier = modifier,
//                            state = state
//                        ){
//                            items(postList.size){
//                                NewsItem(
//                                    navigateToNewsDetail = navigateToNewsDetail,
//                                    post = postList[it]
//                                )
//                            }
//                        }
//                    }
//                }
//            )
//        }
        postListFlow.let{ postList ->
            LazyColumn(
                modifier = modifier,
                state = state,
            ){
                items(
                    postList.itemCount,
                ){
                    postList[it]?.let {
                        NewsItem(
                            navigateToNewsDetail = navigateToNewsDetail,
                            post = it
                        )
                    }
                }
                postList.loadState.apply {
                    when{
                        refresh is LoadState.Loading || append is LoadState.Loading ->{
                            item {
                                Box(modifier = Modifier.fillMaxWidth()){
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .align(Alignment.Center)
                                            .size(30.dp)
                                    )
                                }
                            }
                        }
                        refresh is LoadState.Error || append is LoadState.Error ->{
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.Red)
                                ){
                                    Text(
                                        modifier = Modifier
                                            .padding(vertical = 3.dp)
                                            .fillMaxWidth(),
                                        text = "加载失败",
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

            }
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
        FloatingActionButton(
            onClick = {
                navigateToRelease()
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NewsItem(
    navigateToNewsDetail: (String) -> Unit,
    post: Data,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .clickable {
            navigateToNewsDetail.invoke(post.Id.toString())
        }
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
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = modifier,
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    navigateToNewsDetail.invoke(post.Id.toString())
                }
                .padding(10.dp)
        ) {
            PersonalInformationAreaInList(
                userAvatar = post.User.avatar,
                userName = post.User.username
            )
//            Surface (
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//            ){
//                FlowRow(
//                    modifier = Modifier
//                        .fillMaxSize(),
//                ) {
//                    repeat(30) {
//                        Label("#"+"s"*((1..10).random()))
//                    }
//                }
//            }
            post.FirstImage?.let{
                if(it.isEmpty()){
                    return@let
                }
                KamelImage(
                    resource = asyncPainterResource("${PostImage}/${it}"),
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
                maxLines = lines,
                overflow = TextOverflow.Ellipsis,
                fontSize = 10.sp,
                text = post.LittleDescribe?:""
            )
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){

                Text(
                    modifier = Modifier.weight(1f),
                    text = post.Time.toEasyTime(),
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
            Interaction(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .wrapContentHeight(),
                likeNumber = post.LikeNum
            )
            Divider(modifier = Modifier.padding( top = 3.dp ).fillMaxWidth(),thickness = 1.dp)
        }

    }
}

operator fun String.times(int: Int): String {
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
                .wrapContentSize(Alignment.Center)
                .fillMaxSize(0.7f)
                .clip(RoundedCornerShape(10)),
            contentScale = ContentScale.FillBounds
        )
        Text(
            modifier = Modifier
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
    modifier: Modifier,
    likeNumber :Int
){
    BottomNavigation(
        contentColor = Color.Transparent,
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        modifier = modifier
    ){
        BottomNavigationItem(
            icon = { Icon(
                Icons.Filled.Favorite, contentDescription = null,
                modifier = Modifier
                    .size(15.dp)
            ) },
            label = { Text(likeNumber.toString()) },
            selected = false,
            onClick = {  },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Info, contentDescription = null,
                modifier = Modifier
                    .size(15.dp)) },
            label = { Text("举报",
                fontSize = 10.sp) },
            selected = false,
            onClick = { },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Share, contentDescription = null,
                modifier = Modifier
                    .size(15.dp)) },
            label = { Text("分享", fontSize = 10.sp) },
            selected = false,
            onClick = { },
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
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

@Composable
fun PersonalInformationAreaInList(
    userAvatar : String = "defaultAvatar.jpg",
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
            resource = asyncPainterResource("${BaseUrlConfig.BaseUrl}/static/userAvatar/${userAvatar}"),
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
                .weight(1f)
                .wrapContentHeight(),
            text = userName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

