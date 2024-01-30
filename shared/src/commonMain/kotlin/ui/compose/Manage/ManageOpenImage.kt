package ui.compose.Manage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import config.BaseUrlConfig
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.example.library.MR
import org.koin.compose.koinInject
import ui.util.network.CollectWithContent

class ManageOpenImage(
    buildContext: BuildContext
):Node(
    buildContext = buildContext
){
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun View(modifier: Modifier) {
        val manageViewModel = koinInject<ManageViewModel>()
        LaunchedEffect(Unit){
            manageViewModel.getOpenImage()
        }
        val pageState = rememberPagerState {
            2
        }
        val scope = rememberCoroutineScope()
        Column(
            modifier = Modifier
                .fillMaxSize()
        ){
            HorizontalPager(
                state = pageState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (it) {
                    0 -> {
                        manageViewModel.openImageList.collectAsState().CollectWithContent(
                            success = {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    it.forEach {
                                        item {
                                            OpenImageShow(
                                                it,
                                                refresh = {
                                                    manageViewModel.refresh()
                                                }
                                            )
                                        }
                                    }
                                }
                            },
                            error = {

                            },
                            loading = {
                                Column(
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    CircularProgressIndicator()
                                    Text("加载中")
                                }
                            }
                        )
                    }

                    1 -> {

                    }
                }
            }
            Row (
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.Center
            ){
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            pageState.animateScrollToPage(0)
                        }
                    }
                ){
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ){
                        Icon(
                            painter = painterResource(MR.images.image_now),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            "管理已有"
                        )
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            pageState.animateScrollToPage(1)
                        }
                    }
                ){
                    Row (
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(vertical = 10.dp, horizontal = 20.dp)
                    ){
                        Icon(
                            painter = painterResource(MR.images.image),
                            contentDescription = null,
                            modifier = Modifier
                                .size(30.dp)
                        )
                        Text(
                            "添加新开屏"
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OpenImageShow(
    string: String,
    refresh:()->Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth(1f)
            .aspectRatio(1f),
    ) {
        KamelImage(
            resource = asyncPainterResource("${BaseUrlConfig.openImage}/${string}"),
            modifier = Modifier
                .aspectRatio(0.56f)
                .fillMaxHeight(),
            contentDescription = "",
            contentScale = ContentScale.FillBounds
        )
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .weight(1f)
        ) {
            Button(
                onClick = {
                    refresh.invoke()
                }
            ){
                Text("删除该开屏页")
            }
            Button(
                onClick = {
                    refresh.invoke()
                }
            ){
                Text("删除该开屏页")
            }
        }
    }
}