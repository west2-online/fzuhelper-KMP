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
import cafe.adriel.voyager.core.screen.Screen
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.liftric.kvault.KVault
import config.BaseUrlConfig
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.root.RootAction
import ui.root.RootTarget
import util.compose.EasyToast
import util.compose.rememberToastState
import util.compose.shimmerLoadingAnimation
import util.network.CollectWithContent
import util.network.logicWithType

class SplashPageRouteNode(
    val buildContext: BuildContext
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel:SplashPageViewModel = koinInject()
        val imageState = viewModel.imageState.collectAsState()
        val toast = rememberToastState()
        val rootAction = koinInject<RootAction>()
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
        val kVault = koinInject<KVault>()
        val token : String? = kVault.string(forKey = "token")
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable {
//                    viewModel.navigateToMain()
                    token?.let {
                        rootAction.replaceNewTarget(RootTarget.Main)
                    }
                    token?:let {
                        rootAction.replaceNewTarget(RootTarget.Authentication)
                    }
                }
        ){
            imageState.CollectWithContent(
                success = {
                    KamelImage(
                        resource = asyncPainterResource("${BaseUrlConfig.openImage}/$it"),
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
}

class SplashPageVoyagerScreen():Screen{
    @Composable
    override fun Content() {
        val viewModel:SplashPageViewModel = koinInject()
        val imageState = viewModel.imageState.collectAsState()
        val toast = rememberToastState()
        val rootAction = koinInject<RootAction>()
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
        val kVault = koinInject<KVault>()
        val token : String? = kVault.string(forKey = "token")
        Box(
            modifier = Modifier.fillMaxSize()
                .clickable {
//                    viewModel.navigateToMain()
                    token?.let {
//                        rootAction.replaceNewTarget(RootTarget.Main)
//                        navigator.replace(Main)
//                        navigator
                        rootAction.navigateFormSplashToMainPage()
                    }
                    token?:let {
//                        rootAction.replaceNewTarget(RootTarget.Authentication)
                    }
                }
        ){
            imageState.CollectWithContent(
                success = {
                    KamelImage(
                        resource = asyncPainterResource("${BaseUrlConfig.openImage}/$it"),
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
}