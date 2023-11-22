package ui.compose.New

import BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import asImageBitmap
import data.post.PostById.ImageData
import data.post.PostById.PostById
import data.post.PostById.PostContent
import data.post.PostById.ValueData
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
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
    val currentData = remember {
        mutableStateListOf<PostContent>()
    }
    val scope = rememberCoroutineScope()
    val client = koinInject<HttpClient>()
    LaunchedEffect(postState.value.key){
        postState.value.let {
             when(it){
                is NetworkResult.Success<PostById> -> {
                    it.data.data.valueData.forEach {
                        currentData.add(it)
                    }

                    it.data.data.fileData.forEach {
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
                                userName = postById.data.Post.User.username
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
            Text("你好" * (10..60).random())
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