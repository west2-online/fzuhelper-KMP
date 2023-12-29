package ui.compose.Post

import BackHandler
import ImagePickerFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
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
import data.post.PostById.FileData
import data.post.PostById.PostById
import data.post.PostById.PostContent
import data.post.PostById.ValueData
import data.post.PostComment.Data
import data.post.PostCommentTree.MainComment
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.library.MR
import ui.compose.Report.ReportType
import ui.util.compose.Toast
import ui.util.compose.shimmerLoadingAnimation
import ui.util.network.CollectWithContent
import ui.util.network.NetworkResult
import ui.util.network.logicWithType
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
    getPostCommentTree: (String) -> Unit,
    submitComment: (parentId: Int, postId: Int, tree: String, content: String, image: ByteArray?) -> Unit,
    commentSubmitState: State<NetworkResult<String>>,
    toastState: Toast,
    commentReport:(type:ReportType)->Unit
) {
    val data = postCommentPreview.collectAsLazyPagingItems()
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
                getPostById(id)
                data.refresh()
                isRefresh.value = false
            }
    }

    LaunchedEffect(commentSubmitState.value.key){
        if(!commentSubmitState.value.showToast){
            return@LaunchedEffect
        }
        commentSubmitState.value.logicWithType(
            success = {
                toastState.addToast(it)
            },
            error = {
                toastState.addToast(it.message.toString(), Color.Red)
            }
        )
    }
    val show = remember {
        mutableStateOf<MainComment?>(null)
    }
    LaunchedEffect(show.value){
        show.value?.let {
            getPostCommentTree(it.Id.toString())
        }
    }
    val commentState = remember {
        mutableStateOf<CommentState>(CommentState())
    }

    BackHandler(back != null){
        back?.invoke()
    }

    LaunchedEffect(Unit){
        getPostById(id)
    }

    val scope = rememberCoroutineScope()
    Box(modifier = Modifier
        .fillMaxSize()
    ){
        LazyColumn(
            modifier = modifier,
            state = state
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize()
                ) {
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
                                listOf<PostContent>().plus(postById.data.valueData ?: listOf())
                                    .plus(postById.data.fileData ?: listOf()).sortedBy {
                                    it.order
                                }.forEach {
                                    when (it) {
                                        is FileData -> {
                                            ImageContent(it.fileName)
                                        }

                                        is ValueData -> {
                                            Text(it.value)
                                        }
                                    }
                                }
                                Interaction(
                                    modifier = Modifier
                                        .padding( top = 5.dp )
                                        .fillMaxWidth(0.6f)
                                        .wrapContentHeight(),
                                    likeNumber = postById.data.Post.LikeNum
                                )
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
                Divider(
                    modifier = Modifier.padding(top = 10.dp).fillMaxWidth().padding(bottom = 10.dp)
                )
            }
            items(data.itemCount) {
                CommentInNewsDetail(
                    data[it],
                    click = {
                        scope.launch {
                            data[it]?.let {
                                show.value = it.MainComment
                            }
                        }
                    },
                    report = {
                        commentReport.invoke(
                            ReportType.CommentReportType(id,it.Id.toString(),it)
                        )
                    }
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        FloatingActionButton(
            onClick = {
                commentState.value.setCommentAt(null)
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
                painter = painterResource(MR.images.comment),
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
                    val isRefreshInCommentTree = remember{
                        mutableStateOf(false)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        val commentTree = postCommentTree.value?.flow?.collectAsLazyPagingItems()
                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                null,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(
                                        CircleShape
                                    )
                                    .clickable {
                                        scope.launch {
                                            isRefreshInCommentTree.value = true
                                            delay(1000)
                                            commentTree?.refresh()
                                            isRefreshInCommentTree.value = false
                                        }
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.6f)
                            )
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
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                        ){
                            commentTree?.let {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    item {
                                        Box(
                                            modifier = Modifier
                                                .animateContentSize()
                                        ) {
                                            show.value?.let {
                                                CommentInNewsDetail(
                                                    data = Data(
                                                        MainComment = it,
                                                        SonComment = listOf()
                                                    ), click = {
                                                        commentState.value.setCommentAt(it)
                                                    },
                                                    report = {
                                                        commentReport.invoke(
                                                            ReportType.CommentReportType(id,it.Id.toString(),it)
                                                        )
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    item {
                                        Divider(
                                            modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                                                .padding(bottom = 10.dp)
                                        )
                                    }
                                    items(commentTree.itemCount) {
                                        commentTree[it]?.let { comment ->
                                            CommentTree(
                                                comment,
                                                click = {
                                                    commentState.value.setCommentAt(it)
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                           androidx.compose.animation.AnimatedVisibility(
                               isRefreshInCommentTree.value,
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
                }
            }
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        AnimatedVisibility(
            visible = commentState.value.isShow.value,
            exit = slideOutVertically {
                return@slideOutVertically it
            },
            enter = slideInVertically {
                return@slideInVertically it
            },
            modifier = Modifier
                .fillMaxSize(),
        ){
            val commentValue = remember {
                mutableStateOf("")
            }
            BackHandler(commentState.value.isShow.value){
                commentState.value.closeCommentAt()
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
                                commentState.value.closeCommentAt()
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
                        val imageByteArray = remember {
                            mutableStateOf<ByteArray?>(null)
                        }
                        val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
                        imagePicker.registerPicker(
                            onImagePicked = {
                                imageByteArray.value = it
                            }
                        )
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
                                        commentState.value.closeCommentAt()
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.6f)
                            )
                        }
                        LazyColumn (
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(1f)
                        ){
                            commentState.value.commentAt.value?.let {
                                item {
                                    Column(
                                        modifier = Modifier
                                            .padding(bottom = 10.dp)
                                            .border(
                                                width = 1.dp,
                                                shape = RoundedCornerShape(5.dp),
                                                color = Color.Gray
                                            )
                                            .padding(5.dp)
                                    ) {
                                        Text(
                                            text = "回复@"
                                        )
                                        CommentInNewsDetail(
                                            data = Data(
                                                it,
                                                listOf()
                                            ),
                                            report = {
                                                commentReport.invoke(
                                                    ReportType.CommentReportType(id,it.Id.toString(),it)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                            item {
                                TextField(
                                    value = commentValue.value,
                                    onValueChange = {
                                        commentValue.value = it
                                    } ,
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .animateContentSize()
                                )
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .animateContentSize()
                                ){
                                    imageByteArray.value?.let {
                                        Image(
                                            modifier = Modifier
                                                .fillMaxWidth(0.5f)
                                                .wrapContentHeight()
                                                .clip(RoundedCornerShape(3.dp)),
                                            bitmap = it.asImageBitmap(),
                                            contentDescription = null
                                        )
                                    }
                                }
                            }
                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(bottom = 5.dp)
                                ) {
                                    Crossfade(
                                        imageByteArray.value
                                    ){
                                        if(it == null){
                                            Icon(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(RoundedCornerShape(5.dp))
                                                    .clickable {
                                                        imagePicker.pickImage()
                                                    }
                                                    .padding(7.dp),
                                                painter = painterResource(MR.images.image),
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }else{
                                            Icon(
                                                modifier = Modifier
                                                    .size(50.dp)
                                                    .clip(RoundedCornerShape(5.dp))
                                                    .clickable {
                                                        imageByteArray.value = null
                                                    }
                                                    .padding(7.dp),
                                                imageVector = Icons.Filled.Close,
                                                contentDescription = null,
                                                tint = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                Button(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp),
                                    onClick = {
                                        submitComment.invoke(
                                            if(commentState.value.commentAt.value == null) -1 else commentState.value.commentAt.value!!.Id,
                                            id.toInt(),
                                            if(commentState.value.commentAt.value == null) "-1/" else commentState.value.commentAt.value!!.Tree+commentState.value.commentAt.value!!.Id+"/",
                                            commentValue.value,
                                            imageByteArray.value
                                        )
                                    }
                                ){
                                    Icon(
                                        modifier = Modifier
                                            .padding(end = 5.dp)
                                            .size(30.dp)
                                            .padding(5.dp),
                                        imageVector = Icons.Filled.Done,
                                        contentDescription = null
                                    )
                                    Text("提交评论")
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


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CommentInNewsDetail(
    data: Data?,
    click : ()->Unit ={},
    report : (comment:MainComment)->Unit ={}
) {
    data?.let {
        Row(modifier = Modifier
            .padding(bottom = 10.dp)
            .animateContentSize()
            .clip(RoundedCornerShape(3.dp))
            .combinedClickable(
                onClick = {
                    click()
                },
                onLongClick = {
                    report.invoke(data.MainComment)
                }
            )
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
    imageData: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    ){
//        "static/post/${it.fileName}"
        KamelImage(
            resource = asyncPainterResource("${BaseUrlConfig.PostImage}/${imageData}"),
            null,
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun CommentTree(
    data : data.post.PostCommentTree.Data,
    click: (data.post.PostCommentTree.MainComment) -> Unit,
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
                        .clickable {
                            click.invoke(data.MainComment)
                        }
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
                                        .clip(RoundedCornerShape(5.dp))
                                        .border(
                                            width = 1.dp,
                                            shape = RoundedCornerShape(3.dp),
                                            brush = Brush.linearGradient(listOf(Color(174, 174, 174),Color(174, 174, 174)))
                                        )
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


class CommentState (){
    val isShow: MutableState<Boolean> = mutableStateOf(false)
    val commentAt: MutableState<data.post.PostCommentTree.MainComment?> = mutableStateOf(null)
    fun setCommentAt(mainComment: data.post.PostCommentTree.MainComment?){
        commentAt.value = mainComment
        isShow.value = true
    }

    fun closeCommentAt(){
        isShow.value = false
        commentAt.value = null
    }
}