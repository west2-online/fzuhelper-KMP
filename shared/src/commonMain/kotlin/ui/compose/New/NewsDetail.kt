package ui.compose.New

import BackHandler
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.post.PostById.FileData
import data.post.PostById.PostById
import data.post.PostById.PostContent
import data.post.PostById.ValueData
import io.kamel.core.Resource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import ui.util.compose.shimmerLoadingAnimation
import ui.util.network.CollectWithContent
import ui.util.network.NetworkResult


@Composable
fun NewsDetail(
    id: String,
    modifier: Modifier = Modifier,
    back: (() -> Unit)? = null,
    postState: State<NetworkResult<PostById>>,
    getPostById: () -> Unit
) {
    BackHandler(back!=null){
        back?.invoke()
    }
    LaunchedEffect(Unit){
        getPostById()
    }
    LazyColumn(
            modifier = modifier
    ) {
        item {
            Box (
                modifier = Modifier
                    .fillParentMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize()
            ){
                postState.CollectWithContent(
                    success = { postById ->
                        val contentData = postById.data.fileData.plus<PostContent>(postById.data.valueData).sortedBy {
                            it.order
                        }
                        Column {
                            PersonalInformationAreaInDetail(
                                userName = postById.data.Post.User.username
                            )
                            Time(postById.data.Post.Time)
                            Text(
                                text = postById.data.Post.Title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            contentData.forEach {
                                when(it){
                                    is FileData ->{
                                        ImageContent()
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
        items(10){
            CommentInNewsDetail()
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

interface NewsDetailItemForShow{
    val id : Int
    data class NewsDetailItemForShowText(
        override val id : Int = 0,
        val string: String
    ) : NewsDetailItemForShow

    data class NewsDetailItemForShowImage(
        override val id : Int = 0,
        val url: String
    ) : NewsDetailItemForShow
}

@Composable
fun CommentInNewsDetail(){
    Row(modifier = Modifier.padding(bottom = 10.dp).animateContentSize()){
        KamelImage(
            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
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
            Text("theonenull")
            Text(
                "2023.10.1 10:22",
                fontSize = 10.sp
            )
//            val painter = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg")
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .wrapContentHeight()
//                    .animateContentSize()
//            ){
//                var show by remember {
//                    mutableStateOf(false)
//                }
//                if(painter is Resource.Success<Painter>){
//                    if(painter.value.intrinsicSize.width.toFloat() / painter.value.intrinsicSize.height.toFloat()>50){
//                        Column {
//                            KamelImage(
//                                resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
//                                null,
//                                modifier = Modifier
//                                    .fillMaxWidth()
//                                    .aspectRatio(painter.value.intrinsicSize.width.toFloat() / painter.value.intrinsicSize.height.toFloat()),
//                                contentScale = ContentScale.FillBounds
//                            )
//                            Button(
//                                onClick = {
//                                    show = !show
//                                }
//                            ) {
//                                Text(
//                                    if (show) "收起" else "展开"
//                                )
//                            }
//                        }
//                    }else{
//                        KamelImage(
//                            resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
//                            null,
//                            modifier = Modifier
//                                .fillMaxWidth()
//                                .aspectRatio(painter.value.intrinsicSize.width.toFloat() / painter.value.intrinsicSize.height.toFloat()),
//                            contentScale = ContentScale.FillBounds
//                        )
//                    }
//                }
//                else{
//                    KamelImage(
//                        resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
//                        null,
//                        modifier = Modifier
//                            .padding(top = 10.dp)
//                            .fillMaxWidth()
//                            .aspectRatio(2f),
//                        contentScale = ContentScale.FillBounds
//                    )
//                }
//            }
            Text("你好" * (10..60).random())
        }
    }
}

@Composable
fun ImageContent(){
    val painter = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg")
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .animateContentSize()
    ){
        if(painter is Resource.Success<Painter>){
            KamelImage(
                resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
                null,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .aspectRatio(painter.value.intrinsicSize.width.toFloat() / painter.value.intrinsicSize.height.toFloat()),
                contentScale = ContentScale.FillBounds
            )
        }
        else{
            KamelImage(
                resource = asyncPainterResource("https://pic1.zhimg.com/v2-fddbd21f1206bcf7817ddec207ad2340_b.jpg"),
                null,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxWidth()
                    .aspectRatio(2f),
                contentScale = ContentScale.FillBounds
            )
        }
    }
}