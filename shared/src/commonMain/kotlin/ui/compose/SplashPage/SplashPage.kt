package ui.compose.SplashPage

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.util.compose.shimmerLoadingAnimation

@Composable
fun SplashPage(
    modifier: Modifier,
    viewModel:SplashPageViewModel = koinInject()
){
    var show by remember { mutableStateOf(true) }
    LaunchedEffect(Unit){
        launch {
            delay(1500)
            show = false
        }
    }
    val textList = listOf("落霞与孤鹜齐飞","秋水共长天一色")
    LazyColumn (
        modifier = modifier
            .clickable {
                viewModel.navigateToMain()
            },
    ) {
        item {
            Box(modifier = Modifier.fillMaxWidth().wrapContentHeight().animateContentSize()){
                KamelImage(
                    resource = asyncPainterResource("https://pica.zhimg.com/80/v2-cb2c09a7f53a8ff2c022343a3546bf40_720w.webp?source=1940ef5c"),
                    null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentScale = ContentScale.FillWidth,
                    onLoading = {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.5f)
                            .shimmerLoadingAnimation(
                                colorList = listOf(
                                    Color.Black.copy(alpha = 0.1f),
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.3f),
                                    Color.Black.copy(alpha = 0.2f),
                                    Color.Black.copy(alpha = 0.1f),
                                )
                            )
                            .animateContentSize()
                        )
                    }
                )
            }
        }
        items(textList.size) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                text = textList[it],
                textAlign = TextAlign.End
            )
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            show,
            exit =  slideOutVertically() {
                0
            } + shrinkVertically() + fadeOut(tween(5000)),
        ){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Cyan)
                    .padding(10.dp)
                    .align(Alignment.BottomCenter)
            ){
                Text(
                    "点击屏幕可跳过",
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}