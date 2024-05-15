package ui.compose.Release

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import data.share.Label
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.launch
import org.example.library.MR
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.Toast
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.network.CollectWithContent
import util.network.NetworkResult
import util.network.logicWithTypeWithLimit
import kotlin.jvm.Transient

@Composable
fun ReleasePageScreen(
    modifier: Modifier = Modifier,
    viewModel: ReleasePageViewModel = koinInject(),
    initLabel: List<Label> = listOf()
){
    val toastState = rememberToastState()
    val releasePageItems = remember { mutableStateListOf<ReleasePageItem>() }
    val title = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    var preview by remember { mutableStateOf(false) }
    val labelListFromNetwork = viewModel.labelList.collectAsState()
    val labelList = remember (labelListFromNetwork.value){
        derivedStateOf {
            initLabel.map {
                LabelForSelect(it.Id,it.Value,false,LabelType.Init)
            }.plus(labelListFromNetwork.value.let {
                return@let when(it){
                    is NetworkResult.Success<List<Label>> -> it.dataForShow.map {
                        LabelForSelect(it.Id,it.Value,true,LabelType.Person)
                    }
                    else -> listOf<LabelForSelect>()
                }
            })
        }
    }
    toastState.toastBindNetworkResult(viewModel.newPostState.collectAsState())
    viewModel.newPostState.value.logicWithTypeWithLimit (
        success = {
            releasePageItems.clear()
            labelList.value.forEach {
                it.close()
            }
        }
    )
    LaunchedEffect(Unit){
        viewModel.getUserLabel()
    }

    Column (
        modifier = modifier
    ){
        Crossfade(
            preview,
            modifier = Modifier
                .fillMaxWidth()
                .weight(11f)
                .padding(horizontal = 10.dp)
        ){ isPreview ->
            if (isPreview) {
                PreviewContent(lazyListState, title, releasePageItems,labelList.value)
            } else {
                ReleaseContent(lazyListState, title, releasePageItems,labelList.value,toastState)
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
                    Icon(
                        modifier = Modifier
                            .fillMaxHeight()
                            .aspectRatio(1f)
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.75f)
                            .clip(CircleShape)
                            .clickable {
                                scope.launch{
                                    releasePageItems.add(ReleasePageItem.LineChartItem())
                                    lazyListState.animateScrollToItem(releasePageItems.size - 1)
                                }
                            }
                            .wrapContentSize(Alignment.Center)
                            .fillMaxSize(0.7f)
                        ,
                        painter = painterResource(MR.images.chart_line),
                        contentDescription = null
                    )
                }
//                item{
//                    Crossfade(preview){
//                        if(it){
//                            Icon(
//                                modifier = Modifier
//                                    .fillMaxHeight()
//                                    .aspectRatio(1f)
//                                    .wrapContentSize(Alignment.Center)
//                                    .fillMaxSize(0.75f)
//                                    .clip(CircleShape)
//                                    .clickable {
//                                        scope.launch{
//                                            preview = !preview
//                                        }
//                                    }
//                                    .wrapContentSize(Alignment.Center)
//                                    .fillMaxSize(0.7f)
//                                ,
//                                painter = painterResource(MR.images.eye),
//                                contentDescription = null
//                            )
//                        }else{
//                            Icon(
//                                modifier = Modifier
//                                    .fillMaxHeight()
//                                    .aspectRatio(1f)
//                                    .wrapContentSize(Alignment.Center)
//                                    .fillMaxSize(0.75f)
//                                    .clip(CircleShape)
//                                    .clickable {
//                                        scope.launch{
//                                            preview = !preview
//                                        }
//                                    }
//                                    .wrapContentSize(Alignment.Center)
//                                    .fillMaxSize(0.7f)
//                                ,
//                                painter = painterResource(MR.images.eye_outline),
//                                contentDescription = null
//                            )
//                        }
//                    }
//                }
            }
            FloatingActionButton(
                onClick = {
                    viewModel.getUserLabel()
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ){
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
            FloatingActionButton(
                onClick = {
                    scope.launch {
                        viewModel.getUserLabel()
                    }
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
                    scope.launch {
                        viewModel.newPost(releasePageItems.toList(),title.value,labelList.value.filter { it.isSelect.value }.toList().map { it.id })
                    }
                },
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ){
                viewModel.newPostState.collectAsState().CollectWithContent(
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .wrapContentSize(Alignment.Center)
                                .clip(RoundedCornerShape(10))
                                .fillMaxSize(0.7f),
                        )
                    },
                    content = {
                        Icon(
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .wrapContentSize(Alignment.Center)
                                .clip(RoundedCornerShape(10))
                                .fillMaxSize(0.7f),
                            imageVector = Icons.Filled.Done,
                            contentDescription = null,
                            tint = Color.Green
                        )

                    }
                )
            }
        }
    }
    EasyToast(toastState)
}








val emojiList = listOf("😃","😄","😁","😆","😅","🤣","😂","🙂","😉","😊","😇","🥰","😍","🤩","😘","😗","😙","😏","😋","😛","😜","🤪","😝","🤗","🤭","🤫","🤔","🤤","🤠","🥳","😎","🤓","🧐","🙃","🤐","🤨","😐","😑","😶","😶","😒","🙄","😬","😮","🤥","😌","😔","😪","😴","😷","🤒","🤕","🤢","🤮","🤧","🥵","🥶","🥴","😵","😵","🤯","🥱","😕","😟","🙁","😮","😯","😲","😳","🥺","😦","😧","😨","😰","😥","😢","😭","😱","😖","😣","😞","😓","😩","😫","😤","😡","😠","🤬","👿")





@OptIn(ExperimentalFoundationApi::class, ExperimentalLayoutApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
fun ReleaseContent(
    lazyListState : LazyListState,
    title:MutableState<String>,
    releasePageItems:SnapshotStateList<ReleasePageItem>,
    labelList : List<LabelForSelect>,
    toast: Toast
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
                            label.changeSelected(toast = toast)
                        },
                        label = {
                            Text(
                                "#${label.label}",
                                fontSize = 12.sp
                            )
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

                            is ReleasePageItem.LineChartItem -> {
                                ReleasePageItemLineChart(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    releasePageItem,
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



class ReleaseRouteVoyagerScreen(
    private val initLabel : List<Label> = listOf(),
    @Transient
    val parentPaddingControl : ParentPaddingControl = defaultSelfPaddingControl()
):Screen{
    @Composable
    override fun Content() {
        ReleasePageScreen(
            modifier = Modifier
                .fillMaxSize()
                .parentSystemControl(parentPaddingControl),
            initLabel = initLabel
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
    val id:Int,
    val label : String,
    private val canChange :Boolean = true,
    val labelType: LabelType = LabelType.Official
){
    private val _isSelect: MutableState<Boolean> = mutableStateOf(false)
    val isSelect : State<Boolean> = _isSelect
    fun changeSelected(toast: Toast){
        if (canChange){
            _isSelect.value = !_isSelect.value
        }else{
            toast.addWarnToast("该标签必须选中")
        }
    }
    fun close(){
        _isSelect.value = false
    }
}

enum class LabelType{
    Init,
    Official,
    Person
}
