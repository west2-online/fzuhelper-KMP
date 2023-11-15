package ui.compose.SplashPage

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.util.compose.EasyToast
import ui.util.compose.rememberToastState
import ui.util.compose.shimmerLoadingAnimation
import ui.util.network.CollectWithContent
import ui.util.network.logicWithType

@Composable
fun SplashPage(
    modifier: Modifier,
    viewModel:SplashPageViewModel = koinInject()
){

    val imageState = viewModel.imageState.collectAsState()
    val toast = rememberToastState()

    LaunchedEffect(imageState,imageState.value.key){
        imageState.value.logicWithType(
            error = {
                toast.addToast(it.message.toString(), Color.Red)
            }
        )
    }
    LaunchedEffect(Unit){
        toast.addToast("点击屏幕直接进入")
        viewModel.getSplashPageImage()
    }
    var show by remember { mutableStateOf(true) }
    LaunchedEffect(Unit){
        launch {
            delay(1500)
            show = false
        }
    }
    Box(
        modifier = Modifier.fillMaxSize()
            .clickable {
                viewModel.navigateToMain()
            }
    ){
        imageState.CollectWithContent(
            success = {
                KamelImage(
                    resource = asyncPainterResource("http://172.20.10.2:8000/openImage/$it"),
                    null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.FillBounds,
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
            },
            error = {
                Box( modifier = Modifier.fillMaxSize() ){
                    Text("获取失败",modifier = Modifier.align(Alignment.Center))
                }
            },
            content = {
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
            },
            modifier = Modifier
                .fillMaxSize()
        )

    }
    EasyToast(toast)
}