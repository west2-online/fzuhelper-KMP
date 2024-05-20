package ui.compose.Release

import ImagePickerFactory
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import asImageBitmap
import com.aay.compose.lineChart.model.LineType
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import org.example.library.MR

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
        val animateChart = mutableStateOf(true)
        val lineShadow = mutableStateOf(true)
        val lineType = mutableStateOf(LineType.CURVED_LINE)
        val isGrid = mutableStateOf<Boolean>(true)
        val gridColor = mutableStateOf<Color>(Color.Gray)
        val label = mutableStateOf<String>("")
        val lineParameters = mutableStateListOf<LineParametersDataItem>()
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
class LineParametersDataItem {
    val x = mutableStateOf<String>("")
    val y = mutableStateOf<String>("")
}

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
            (image.value?.size?:0)/1024/1024 > 2 ,
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
    lineChartItem: ReleasePageItem.LineChartItem,
    delete:()->Unit = {},
    moveUp:()->Unit = {},
    moveDown: () -> Unit = {},
){
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
            value = lineChartItem.label.value,
            onValueChange = {
                lineChartItem.label.value = it
            },
            label = {
                Text("标题(请尽量缩短长度)")
            }
        )
        LazyRow {
            item {
                Column (
                    modifier = Modifier
                        .padding(top = 50.dp)
                ){
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .wrapContentWidth()
                            .height(56.dp),
                    ){
                        Text(
                            text = "X",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                    Box(
                        modifier = Modifier
                            .padding(5.dp)
                            .wrapContentWidth()
                            .height(56.dp),
                    ){
                        Text(
                            text = "Y",
                            modifier = Modifier
                                .align(Alignment.Center)
                        )
                    }
                }
            }
            lineChartItem.lineParameters.forEach { lineParameter ->
                item {
                    Column {
                        Row (
                            modifier = Modifier
                                .height(50.dp)
                        ){
                            IconButton(
                                onClick = {
                                    lineChartItem.lineParameters.remove(lineParameter)
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = null
                                    )
                                },

                                )
                        }
                        TextField(
                            value = lineParameter.x.value,
                            onValueChange = {
                                lineParameter.x.value = it
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .wrapContentWidth()
                                .height(56.dp)
                        )
                        TextField(
                            value = lineParameter.y.value.toString(),
                            onValueChange = {
                                lineParameter.y.value = it
                            },
                            modifier = Modifier
                                .padding(5.dp)
                                .wrapContentWidth()
                                .height(56.dp),
                            isError = try {
                                lineParameter.y.value.toFloat()
                                false
                            }catch (e:Exception){
                                true
                            }
                        )
                    }
                }
            }
            item {
                Row (
                    modifier = Modifier
                        .height(50.dp)
                ){
                    IconButton(
                        onClick = {
                            lineChartItem.lineParameters.add(LineParametersDataItem())
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null
                            )
                        },
                    )
                }
            }
        }
        AnimatedVisibility(setting.value){
            Column {
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        "折线颜色:",
                    )
                    LazyRow {
                        listOf(
                            Color.Green,
                            Color.Red,
                            Color.Gray,
                            Color.Blue,
                            Color.Cyan,
                            Color.Magenta,
                            Color.Black
                        ).forEach {
                            item {
                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .composed {
                                            if (lineChartItem.gridColor.value == it){
                                                return@composed this.border(1.dp,shape = RoundedCornerShape(0),color = Color.Black)
                                            }
                                            else return@composed this
                                        }
                                        .clickable {
                                            lineChartItem.gridColor.value = it
                                        }
                                        .padding(5.dp)
                                        .background(it)
                                )
                            }
                        }
                    }

                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        "显示阴影: 当前 “${if (lineChartItem.lineShadow.value) "是" else "否"}”",
                    )
                    Button(
                        onClick = {
                            lineChartItem.lineShadow.value = !lineChartItem.lineShadow.value
                        }
                    ){
                        Text("修改")
                    }
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        "显示表格: 当前 “${if (lineChartItem.isGrid.value) "是" else "否"}”",
                    )
                    Button(
                        onClick = {
                            lineChartItem.isGrid.value = !lineChartItem.isGrid.value
                        }
                    ){
                        Text("修改")
                    }
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        "显示动画: 当前 “${if (lineChartItem.animateChart.value) "是" else "否"}”",
                    )
                    Button(
                        onClick = {
                            lineChartItem.animateChart.value = !lineChartItem.animateChart.value
                        }
                    ){
                        Text("修改")
                    }
                }
                Row (
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    Text(
                        "折线颜色:",
                    )
                    LazyRow {
                        item {
                            Box(
                                modifier = Modifier
                                    .composed {
                                        if (lineChartItem.lineType.value == LineType.CURVED_LINE){
                                            return@composed this.border(1.dp,shape = RoundedCornerShape(0),color = Color.Black)
                                        }
                                        else return@composed this
                                    }
                                    .clickable {
                                        lineChartItem.lineType.value = LineType.CURVED_LINE
                                    }
                                    .padding(5.dp)
                            ){
                                Text("曲线",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        }
                        item {
                            Box(
                                modifier = Modifier
                                    .composed {
                                        if (lineChartItem.lineType.value == LineType.DEFAULT_LINE){
                                            return@composed this.border(1.dp,shape = RoundedCornerShape(0),color = Color.Black)
                                        }
                                        else return@composed this
                                    }
                                    .clickable {
                                        lineChartItem.lineType.value = LineType.DEFAULT_LINE
                                    }
                                    .padding(5.dp)
                            ){
                                Text(
                                    "折线",
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        }
                    }

                }
            }
        }
    }
}



