package ui.compose.Post.PostDisplayShare
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import asImageBitmap
import ui.compose.Release.ReleasePageItem

/**
 * 文本预览
 * @param modifier Modifier
 * @param text State<String>
 */
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

/**
 * 图片预览
 * @param modifier Modifier
 * @param image MutableState<ByteArray?>
 */
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

/**
 * 折线图预览
 * @param releasePageItem LineChartItem
 */
@Composable 
fun ReleasePageItemLineChartForShow(
    releasePageItem : ReleasePageItem.LineChartItem
){
    XYChart(
        false,
        data = releasePageItem.toXyLineChartData()
    )
}





