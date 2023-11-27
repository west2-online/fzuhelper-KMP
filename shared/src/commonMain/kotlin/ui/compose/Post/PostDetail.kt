package ui.compose.Post

import BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.PagingData
import app.cash.paging.Pager
import app.cash.paging.compose.collectAsLazyPagingItems
import asImageBitmap
import config.BaseUrlConfig
import data.post.PostById.ImageData
import data.post.PostById.PostById
import data.post.PostById.PostContent
import data.post.PostById.ValueData
import data.post.PostComment.Data
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import ui.util.compose.shimmerLoadingAnimation
import ui.util.network.CollectWithContent
import ui.util.network.NetworkResult
import ui.util.network.toEasyTime

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun NewsDetail(
    id: String,
    modifier: Modifier = Modifier,
    back: (() -> Unit)? = null,
    postState: State<NetworkResult<PostById>>,
    getPostById: (String) -> Unit,
    postCommentPreview: Flow<PagingData<Data>>,
    postCommentTree: StateFlow<Pager<Int, data.post.PostCommentTree.Data>?>,
    getPostCommentTree: (String)->Unit
) {
    val show = remember {
        mutableStateOf<data.post.PostComment.MainComment?>(null)
    }
    LaunchedEffect(show.value){
        show.value?.let {
            getPostCommentTree(it.Id.toString())
        }
    }
    val data = postCommentPreview.collectAsLazyPagingItems()
    BackHandler(back!=null){
        back?.invoke()
    }
    LaunchedEffect(Unit){
        getPostById(id)
    }
    val currentData = remember {
        mutableStateListOf<PostContent>()
    }
    val scope = rememberCoroutineScope()
    val client = koinInject<HttpClient>()
    LaunchedEffect(postState.value.key){
        postState.value.let {
             when(it){
                is NetworkResult.Success<PostById> -> {
                    it.data.data.valueData?.forEach {
                        currentData.add(it)
                    }

                    it.data.data.fileData?.forEach {
                        scope.launch(Dispatchers.IO) {
                            val byteArray = client.get("static/post/${it.fileName}").readBytes()
                            val data = ImageData(
                                order = it.order,
                                imageData = byteArray,
                                fileName = it.fileName
                            )
                            currentData.add(data)
                        }
                    }
                }
                else ->{

                }
            }
        }
    }
    LazyColumn(
        modifier = modifier
    ) {
        item (key = postState.value.key.value){
            Box (
                modifier = Modifier
                    .fillParentMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize()
            ){
                postState.CollectWithContent(
                    success = { postById ->
                        Column {
                            PersonalInformationAreaInDetail(
                                userName = postById.data.Post.User.username,
                                url = "${BaseUrlConfig.UserAvatar}/${postById.data.Post.User.avatar}"
                            )
                            Time(postById.data.Post.Time)
                            Text(
                                text = postById.data.Post.Title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            currentData.sortedBy {
                                it.order
                            }.forEach {
                                when(it){
                                    is ImageData ->{
                                        ImageContent(it.imageData)
                                    }
                                    is ValueData ->{
                                        Text(it.value)
                                    }
                                }
                            }
                        }
                    },
                    loading = {
                        CircularProgressIndicator()
                    },
                    content = {
                        Spacer(modifier = Modifier.height(1.dp))
                    }
                )
            }
        }
        item {
            Divider(modifier = Modifier.padding(top = 10.dp).fillMaxWidth().padding(bottom = 10.dp))
        }
        items(data.itemCount){
            CommentInNewsDetail(
                data[it],
                click = {
                    scope.launch {
                        data[it]?.let {
                            show.value = it.MainComment
                        }
                    }
                }
            )
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        AnimatedVisibility(
            visible = show.value != null,
            exit = slideOutVertically {
                return@slideOutVertically it
            },
            enter = slideInVertically {
                return@slideInVertically it
            },
            modifier = Modifier
                .fillMaxSize(),
        ){
            BackHandler(show.value != null){
                show.value = null
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(216, 211, 210, 150))
                        .clickable(
                            remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                show.value = null
                            }
                        )
                )
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f)
                        .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.KeyboardArrowDown,
                                null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .clickable {
                                        show.value = null
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.6f)
                            )
                        }
                        postCommentTree.value?.let {
                            val commentTree = it.flow.collectAsLazyPagingItems()
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth()
                            ) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .animateContentSize()
                                    ){
                                        show.value?.let {
                                            CommentInNewsDetail(
                                                data = Data(
                                                    MainComment = it,
                                                    SonComment = listOf()
                                                ), click = {}
                                            )
                                        }
                                    }
                                }
                                item {
                                    Divider(modifier = Modifier.padding(top = 10.dp).fillMaxWidth().padding(bottom = 10.dp))
                                }
                                items(commentTree.itemCount){
                                    commentTree[it]?.let { comment ->
                                        CommentTree(
                                            comment,
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




@Composable
fun Time(time:String){
    val instant = Instant.parse(time)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    Text(
        text = "${ localDateTime.date.toString() } ${localDateTime.hour}:${localDateTime.minute}",
        fontSize = 10.sp
    )
}

@Composable
fun PersonalInformationAreaInDetail(
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
                .fillMaxHeight(0.7f)
                .aspectRatio(1f)
                .clip(CircleShape),
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


fun LazyListScope.newsDetailItem(
    block:@Composable ()->Unit
){
    item {
        Box(
            modifier = Modifier
                .padding(start = 50.dp)
        ){
            block()
        }
    }
}


@Composable
fun CommentInNewsDetail(
    data: Data?,
    click : ()->Unit ={}
) {
    data?.let {
        Row(modifier = Modifier
            .padding(bottom = 10.dp)
            .animateContentSize()
            .clip(RoundedCornerShape(3.dp))
            .clickable {
                click()
            }
            .padding(vertical = 5.dp)
        ){
            KamelImage(
                resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${data.MainComment.User.avatar}"),
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
                Text(data.MainComment.User.username)
                Text(
                    data.MainComment.Time.toEasyTime(),
                    fontSize = 10.sp
                )
                Text(
                    data.MainComment.Content,
                    fontSize = 14.sp
                )
                data.MainComment.Image.let {
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

                if(data.SonComment.isNotEmpty()){
                    Surface (
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    ){
                        Column (
                            modifier = Modifier
                                .padding(10.dp)
                        ){
                            data.SonComment.forEach {
                                Text(
                                    "${ it.User.username }: ${ it.Content }",
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier
                                        .padding(bottom = 3.dp)
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ImageContent(
    imageData:ByteArray
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    ){
        Image(
            bitmap = imageData.asImageBitmap(),
            null,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillBounds
        )
    }
}

@Composable
fun CommentTree(
    data : data.post.PostCommentTree.Data
){
    val showParent = remember {
        mutableStateOf(false)
    }
    val rotate: Float by animateFloatAsState(if (showParent.value) 180f else 0f)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ){

        data.MainComment.let {
            Column{
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .animateContentSize()
                        .clip(RoundedCornerShape(3.dp))
                        .padding(vertical = 5.dp)
                ) {
                    KamelImage(
                        resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${it.User.avatar}"),
                        null,
                        modifier = Modifier
                            .size(50.dp)
                            .wrapContentSize(Alignment.TopCenter)
                            .fillMaxSize(0.7f)
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10)),
                        contentScale = ContentScale.FillBounds,
                        onLoading = {
                            Box(
                                modifier = Modifier
                                    .matchParentSize()
                                    .shimmerLoadingAnimation()
                            )
                        }
                    )
                    Column(
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .weight(1f)
                            .wrapContentHeight()
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Column (
                                modifier = Modifier
                                    .weight(1f)
                            ){
                                Text(it.User.username)
                                Text("回复@${data.ParentComment.User.username}", fontSize = 10.sp)
                            }
                            Row (
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .clip(
                                        RoundedCornerShape(10)
                                    )
                                    .clickable {
                                        showParent.value = !showParent.value
                                    }
                                    .padding(start = 3.dp)
                            ){
                                Text(
                                    text = if(showParent.value) "隐藏回复内容" else  "显示回复内容",
                                    fontSize = 9.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    null,
                                    modifier = Modifier
                                        .rotate(rotate)
                                        .size(30.dp)
                                        .clip(CircleShape)
                                )
                            }

                        }
                        Text(
                            it.Time.toEasyTime(),
                            fontSize = 10.sp
                        )
                        Text(
                            it.Content,
                            fontSize = 14.sp
                        )
                        it.Image.let {
                            if (it != "") {
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
                                        Box(
                                            modifier = Modifier
                                                .matchParentSize()
                                                .shimmerLoadingAnimation()
                                        )
                                    }
                                )
                            }
                        }
                        AnimatedVisibility(
                            showParent.value,
                            modifier = Modifier

                        ){
                            data.ParentComment.let {
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = 10.dp)
                                        .animateContentSize()
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(MaterialTheme.colors.primary )
                                        .padding(vertical = 5.dp)
                                ) {
                                    KamelImage(
                                        resource = asyncPainterResource("${BaseUrlConfig.UserAvatar}/${it.User.avatar}"),
                                        null,
                                        modifier = Modifier
                                            .size(50.dp)
                                            .wrapContentSize(Alignment.TopCenter)
                                            .fillMaxSize(0.7f)
                                            .aspectRatio(1f)
                                            .clip(RoundedCornerShape(10)),
                                        contentScale = ContentScale.FillBounds,
                                        onLoading = {
                                            Box(
                                                modifier = Modifier
                                                    .matchParentSize()
                                                    .shimmerLoadingAnimation()
                                            )
                                        }
                                    )
                                    Column(
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .weight(1f)
                                            .wrapContentHeight()
                                    ) {
                                        Text(it.User.username)
                                        Text(
                                            it.Time.toEasyTime(),
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            it.Content,
                                            fontSize = 14.sp
                                        )
                                        it.Image.let {
                                            if (it != "") {
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
                                                        Box(
                                                            modifier = Modifier
                                                                .matchParentSize()
                                                                .shimmerLoadingAnimation()
                                                        )
                                                    }
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
    }
}