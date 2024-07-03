package ui.compose.Release

import ImagePickerFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.TextField
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import asImageBitmap
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import org.example.library.MR
import ui.compose.Post.PostDisplayShare.LineChartData

/**
 * Release page item
 * 发布页的item接口
 * @constructor Create empty Release page item
 */
sealed interface ReleasePageItem{

    /**
     * 文本类
     * @property text MutableState<String>
     */
    class TextItem() : ReleasePageItem{
        var text = mutableStateOf<String>("")
    }

    /**
     * 图片类
     * @property image MutableState<ByteArray?>
     */
    class ImageItem() : ReleasePageItem{
        var image = mutableStateOf<ByteArray?>(null)
    }
    class LineChartItem:ReleasePageItem{
        val xList:MutableList<MutableState<String>> = mutableStateListOf()
        val yMap: SnapshotStateMap<MutableState<String>, SnapshotStateList<MutableState<String>>> = mutableStateMapOf()
        val title = mutableStateOf("")
        val xAxisTitle = mutableStateOf("")
        val yAxisTitle = mutableStateOf("")
        fun toXyLineChartData():LineChartData{
            return LineChartData(
                xData = xList.map {
                     it.value
                },
                yMap = buildMap {
                    yMap.forEach {
                        put(it.key.value,it.value.map{
                            it.value.toFloatOrNull() ?: 1f
                        })
                    }
                },
                xAxisTitle = xAxisTitle.value,
                title = title.value,
                yAxisTitle = yAxisTitle.value
            )
        }
        fun isNotEmpty():Boolean{
            return xList.size == yMap.map {
                it.value.size
            }.min() && xList.size == yMap.map {
                it.value.size
            }.max()
        }
        fun addX(){
            xList.add(mutableStateOf(""))
            yMap.forEach {
                it.value.add(mutableStateOf(""))
            }
        }
        fun addY(){
            yMap.put(
                mutableStateOf("") , buildList {
                    repeat(xList.size){
                        add(mutableStateOf<String>(""))
                    }
                }.toMutableStateList()
            )
        }
    }
    class VotingItem(
        val votingItemData : List<VotingItemData>,
        val title : String
    )
}
data class VotingItemData(
    val name : String,
    val number: Int
)

/**
 * 文本发布item
 * @param modifier Modifier
 * @param onValueChange Function1<String, Unit>
 * @param onEmojiChange Function1<String, Unit>
 * @param overflow Function1<Int, Unit> 文本超过大小时自适应动画结束时的回调
 * @param delete Function0<Unit>
 * @param moveUp Function0<Unit>
 * @param moveDown Function0<Unit>
 * @param text State<String>
 */
@Composable
fun ReleasePageItemText(
    modifier: Modifier,
    onValueChange:(String)->Unit = {},
    onEmojiChange:(String)->Unit = {},
    overflow:(Int)->Unit = {},
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {},
    text: State<String>,
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

/**
 * 图像发布item
 * @param modifier Modifier
 * @param delete Function0<Unit>
 * @param moveUp Function0<Unit>
 * @param moveDown Function0<Unit>
 * @param onImagePicked Function1<ByteArray, Unit> 挑选图片
 * @param image State<ByteArray?>
 */
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
        AnimatedVisibility(
            (image.value?.size?:0)/ 1024 / 8 > 5 ,
            modifier = Modifier
                .wrapContentSize()
                .padding(10.dp)
        ){
            Text("图片过大,无法发送", color = Color.Red)
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
fun ReleasePageItemLineChart(
    modifier: Modifier,
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {},
    dataForRelease : ReleasePageItem.LineChartItem
){
    val state = rememberLazyListState()
    Column (
        modifier = modifier
            .padding(10.dp)
    ){
        val setting = remember {
            mutableStateOf(false)
        }
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
            item {
                Button(
                    onClick = {
                        setting.value = !setting.value
                    }
                ){
                    Text("图表设置")
                }
            }
        }
        TextField(
            value = dataForRelease.title.value,
            onValueChange = {
                dataForRelease.title.value = it
            },
            label = {
                Text("标题(请尽量缩短长度)")
            }
        )
        TextField(
            value = dataForRelease.xAxisTitle.value,
            onValueChange = {
                dataForRelease.xAxisTitle.value = it
            },
            label = {
                Text("X轴标签")
            }
        )
        TextField(
            value = dataForRelease.yAxisTitle.value,
            onValueChange = {
                dataForRelease.yAxisTitle.value = it
            },
            label = {
                Text("Y轴标签")
            }
        )
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = null,
            modifier.clickable {
                dataForRelease.addX()
            }
        )
        Column (
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
        ){
            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ){
                Text(
                    "X列数据",
                    modifier.gridCellWidth()
                )
                VerticalDivider(
                    modifier = Modifier
                        .height(50.dp),
                    thickness = 2.dp,
                    color = Color.Black
                )
                dataForRelease.xList.forEach {
                    TextField(
                        value = it.value,
                        modifier = Modifier.gridCellWidth(),
                        onValueChange = { text ->
                            it.value = text
                        }
                    )
                }
            }
            Divider(
                color = Color.Black,
                modifier = Modifier.width((dataForRelease.xList.size*200 + 200 ).dp),
                thickness = 2.dp
            )
            dataForRelease.yMap.forEach {
                Row (
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ){
                    TextField(
                        value = it.key.value,
                        modifier = Modifier.gridCellWidth(),
                        onValueChange = { text ->
                            it.key.value = text
                        }
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .height(56.dp),
                        thickness = 2.dp,
                        color = Color.Black
                    )
                    it.value.forEach {
                        TextField(
                            value = it.value,
                            modifier = Modifier.gridCellWidth(),
                            onValueChange = { text ->
                                it.value = text
                            }
                        )
                    }
                }
            }
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                modifier.clickable {
                    dataForRelease.addY()
                }
            )
        }
        AnimatedVisibility(setting.value){

        }
    }
}

val GridCellWidth = 200.dp

fun Modifier.gridCellWidth() = this.width(GridCellWidth)

