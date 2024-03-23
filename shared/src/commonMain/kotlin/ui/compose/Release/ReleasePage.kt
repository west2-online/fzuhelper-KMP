package ui.compose.Release

import ImagePickerFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import asImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import kotlinx.coroutines.launch
import org.example.library.MR
import org.koin.compose.koinInject
import ui.compose.Post.times
import util.compose.EasyToast
import util.compose.Label
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReleasePageScreen(
    modifier: Modifier = Modifier,
    viewModel: ReleasePageViewModel = koinInject()
){
    val toastState = rememberToastState()
    val releasePageItems = remember { mutableStateListOf<ReleasePageItem>() }
    val title = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var preview by remember { mutableStateOf(false) }
    val labelList = remember {
        mutableStateListOf<LabelForSelect>()
    }
    LaunchedEffect(Unit){
        repeat(30){
            labelList.add(LabelForSelect("#" + "s" * ((1..10).random())))
        }
    }
    toastState.toastBindNetworkResult(viewModel.newPostState.collectAsState())
    Column (
        modifier = Modifier
            .padding(10.dp)
    ){
        Crossfade(
            preview,
            modifier = Modifier
                .fillMaxWidth()
                .weight(11f)
                .padding(top = 10.dp)
        ){ isPreview ->
            if (isPreview) {
                PreviewContent(lazyListState, title, releasePageItems,labelList)
            } else {
                ReleaseContent(lazyListState, title, releasePageItems,labelList)
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
                            .fillMaxSize(0.75f)
                            .clip(CircleShape)
                            .clickable {
                                scope.launch{
                                    releasePageItems.add(ReleasePageItem.TextItem())
                                    lazyListState.animateScrollToItem(releasePageItems.size - 1)
                                }
                            }
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.7f)
                        ,
                        painter = painterResource(MR.images.pencil_plus),
                        contentDescription = null
                    )
                }
                item{
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.75f)
                            .clip(CircleShape)
                            .clickable {
                                scope.launch{
                                    releasePageItems.add(ReleasePageItem.ImageItem())
                                    lazyListState.animateScrollToItem(releasePageItems.size - 1)
                                }
                            }
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.7f)
                        ,
                        painter = painterResource(MR.images.image),
                        contentDescription = null
                    )
                }
                item{
                    Crossfade(preview){
                        if(it){
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.75f)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch{
                                            preview = !preview
                                        }
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.7f)
                                ,
                                painter = painterResource(MR.images.eye),
                                contentDescription = null
                            )
                        }else{
                            Icon(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.75f)
                                    .clip(CircleShape)
                                    .clickable {
                                        scope.launch{
                                            preview = !preview
                                        }
                                    }
                                    .wrapContentSize(Alignment.Center)
                                    .fillMaxSize(0.7f)
                                ,
                                painter = painterResource(MR.images.eye_outline),
                                contentDescription = null
                            )
                        }
                    }
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
                    viewModel.newPost(releasePageItems.toList(),title.value)
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
    EasyToast(toastState)
}

interface ReleasePageItem{
    class TextItem() : ReleasePageItem{
        var text = mutableStateOf<String>("")
    }
    class ImageItem() : ReleasePageItem{
        var image = mutableStateOf<ByteArray?>(null)
    }

}




@Composable
fun ReleasePageItemText(
    modifier: Modifier,
    onValueChange:(String)->Unit = {},
    onEmojiChange:(String)->Unit = {},
    overflow:(Int)->Unit = {},
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {},
    text:State<String>,
){
    val openEmoji = remember{
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
    ){
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
                                moveDown.invoke()
                            }
                            .fillMaxSize(0.7f)
                        ,
                        imageVector = Icons.Filled.KeyboardArrowDown,
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
                                moveUp.invoke()
                            }
                            .fillMaxSize(0.7f)
                        ,
                        imageVector = Icons.Filled.KeyboardArrowUp,
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
                                openEmoji.value = !openEmoji.value
                            }
                            .fillMaxSize(0.6f)
                        ,
                        painter = painterResource(MR.images.emoji),
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
                        value = TextFieldValue(text.value, TextRange(text.value.length)),
                        onValueChange = { textFieldValue ->
                            onValueChange.invoke(textFieldValue.text)
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
        val textMeasureScope = rememberTextMeasurer()
        val result = textMeasureScope.measure("\uD83D\uDE03")
        Crossfade(openEmoji.value){
            if(it){
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(5),
                    modifier = Modifier.height((result.size.height*5).dp).fillMaxWidth()
                ){
                    items(emojiList){
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(10))
                                .clickable {
                                    onEmojiChange.invoke(it)
                                }
                        ){
                            Text(
                                text = it,
                                modifier = Modifier
                                    .wrapContentSize()
                                    .align(Alignment.Center)
                            )
                        }

                    }
                }
            }
        }
    }
}
val emojiList = listOf("😃","😄","😁","😆","😅","🤣","😂","🙂","😉","😊","😇","🥰","😍","🤩","😘","😗","😙","😏","😋","😛","😜","🤪","😝","🤗","🤭","🤫","🤔","🤤","🤠","🥳","😎","🤓","🧐","🙃","🤐","🤨","😐","😑","😶","😶","😒","🙄","😬","😮","🤥","😌","😔","😪","😴","😷","🤒","🤕","🤢","🤮","🤧","🥵","🥶","🥴","😵","😵","🤯","🥱","😕","😟","🙁","😮","😯","😲","😳","🥺","😦","😧","😨","😰","😥","😢","😭","😱","😖","😣","😞","😓","😩","😫","😤","😡","😠","🤬","👿")
@Composable
fun ReleasePageItemImage(
    modifier: Modifier,
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {},
    onImagePicked : (ByteArray) -> Unit,
    image: State<ByteArray?>
){
    Column( modifier ) {
        val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
        imagePicker.registerPicker(onImagePicked)
        LazyRow(
            modifier = Modifier
                .height(60.dp)
                .padding(vertical = 5.dp)
        ) {
            item{

                Button(
                    {
                        imagePicker.pickImage()
                    }
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
                            moveUp.invoke()
                        }
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.KeyboardArrowUp,
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
                            moveDown.invoke()
                        }
                        .fillMaxSize(0.7f)
                    ,
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = {
                Crossfade(image.value){
                    if(it != null){
                        Image(
                            bitmap = it.asImageBitmap(),
                            null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }
        )
    }
}

@Composable
fun ReleasePageItemTextForShow(
    modifier: Modifier,
    text : State<String>
){
    Column( modifier ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            content = {
                Text(
                    text = text.value,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
        )
    }
}


@Composable
fun ReleasePageItemImageForShow(
    modifier: Modifier,
    image: MutableState<ByteArray?>
){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        content = {
            Crossfade(image.value){
                if(it != null){
                    Image(
                        bitmap = it.asImageBitmap(),
                        null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ReleaseContent(
    lazyListState : LazyListState,
    title:MutableState<String>,
    releasePageItems:SnapshotStateList<ReleasePageItem>,
    labelList : SnapshotStateList<LabelForSelect>
){
    val scope = rememberCoroutineScope()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState
    ) {
        item {
            Column(
                modifier = Modifier
                    .padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .animateContentSize(
                            finishedListener = { init, target ->
                                scope.launch {
                                    lazyListState.animateScrollBy(target.height.toFloat() - init.height.toFloat())
                                }
                            }
                        ),
                    content = {
                        TextField(
                            value = title.value,
                            onValueChange = {
                                title.value = it
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            label = {
                                Text("标题")
                            }
                        )
                    }
                )
            }
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
            ) {
                labelList.forEach { label ->
                    ElevatedFilterChip(
                        leadingIcon = {
                            Crossfade(label.isSelect){
                                if(it.value){
                                    Icon(imageVector = Icons.Filled.Done,contentDescription = null)
                                }
                            }
                        },
                        onClick = {
                            label.isSelect.value = !label.isSelect.value
                        },
                        label = {
                            Text(label.label)
                        },
                        selected = label.isSelect.value,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .wrapContentSize()
                            .animateContentSize(),
                        shape = RoundedCornerShape(20)
                    )
                }
            }
        }
        releasePageItems.toList().forEachIndexed { index, releasePageItem ->
            item {
                Card(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(5),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                            .animateContentSize { initialValue, targetValue ->
                                scope.launch {
                                    lazyListState.animateScrollBy((targetValue.height - initialValue.height).dp.value)
                                }
                            }
                    ) {
                        when (releasePageItem) {
                            is ReleasePageItem.TextItem -> {
                                ReleasePageItemText(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .animateContentSize(),
                                    text = releasePageItem.text,
                                    onValueChange = {
                                        releasePageItem.text.value = it
                                    },
                                    delete = {
                                        releasePageItems.removeAt(index)
                                    },
                                    moveDown = {
                                        releasePageItems.downOrder(index)
                                    },
                                    moveUp = {
                                        releasePageItems.upOrder(index)
                                    },
                                    onEmojiChange = {
                                        releasePageItem.text.value = releasePageItem.text.value + it
                                    }
                                )
                            }

                            is ReleasePageItem.ImageItem -> {
                                ReleasePageItemImage(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .animateContentSize()
                                        .animateItemPlacement(),
                                    onImagePicked = {
                                        releasePageItem.image.value = it
                                    },
                                    image = releasePageItem.image,
                                    delete = {
                                        releasePageItems.removeAt(index)
                                    },
                                    moveDown = {
                                        releasePageItems.downOrder(index)
                                    },
                                    moveUp = {
                                        releasePageItems.upOrder(index)
                                    }
                                )
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
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun PreviewContent(
    lazyListState: LazyListState,
    title: MutableState<String>,
    releasePageItems: SnapshotStateList<ReleasePageItem>,
    labelList: SnapshotStateList<LabelForSelect>
){
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = lazyListState
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    content = {
                        Text(
                            text = title.value,
                            modifier = Modifier
                                .padding(bottom = 5.dp)
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(10.dp),
                            fontSize = 20.sp
                        )
                    }
                )
            }
        }
        item {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .animateContentSize(),
            ) {
                labelList.filter {
                    it.isSelect.value
                }.forEach { label ->
                    Label(label.label)
                }
            }
        }
        releasePageItems.toList().filter {
            return@filter when (it) {
                is ReleasePageItem.TextItem -> it.text.value != ""
                is ReleasePageItem.ImageItem -> it.image.value != null
                else -> false
            }
        }.forEachIndexed { _, releasePageItem ->
            item {
                Box(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(10.dp)
                ) {
                    when (releasePageItem) {
                        is ReleasePageItem.TextItem -> {
                            ReleasePageItemTextForShow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .animateItemPlacement(),
                                text = releasePageItem.text,
                            )
                        }

                        is ReleasePageItem.ImageItem -> {
                            ReleasePageItemImageForShow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .animateContentSize()
                                    .animateItemPlacement(),
                                image = releasePageItem.image
                            )
                        }
                    }
                }
            }
        }
    }
}

class ReleaseRouteVoyagerScreen:Screen{
    @Composable
    override fun Content() {
        ReleasePageScreen(
            modifier = Modifier
                .fillMaxSize()
        )
    }
}

fun SnapshotStateList<ReleasePageItem>.upOrder(index:Int){
    if(index == this.indexOf(this.first())){
        return
    }
    val temp = this[index-1]
    this[index-1] = this[index]
    this[index] = temp
}

fun SnapshotStateList<ReleasePageItem>.downOrder(index:Int){
    if(index == this.indexOf(this.last())){
        return
    }
    val temp = this[index+1]
    this[index+1] = this[index]
    this[index] = temp
}

class LabelForSelect(
    val label : String,
    val isSelect: MutableState<Boolean> = mutableStateOf(false),
)
