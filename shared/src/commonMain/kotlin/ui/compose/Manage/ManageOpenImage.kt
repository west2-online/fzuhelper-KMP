package ui.compose.Manage

import ImagePickerFactory
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import asImageBitmap
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import config.BaseUrlConfig
import dev.icerock.moko.resources.compose.painterResource
import getPlatformContext
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import org.example.library.MR
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.network.CollectWithContent
import util.network.NetworkResult

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
        val imageByteArray = remember {
            mutableStateOf<ByteArray?>(null)
        }
        val imagePicker = ImagePickerFactory(context = getPlatformContext()).createPicker()
        imagePicker.registerPicker(
            onImagePicked = {
                imageByteArray.value = it
            }
        )
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
                Box(modifier = Modifier.fillMaxSize()){
                    when (it) {
                        0 -> {
                            val toastState = rememberToastState()
                            toastState.toastBindNetworkResult(manageViewModel.openImageDelete.collectAsState())
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
                                                    },
                                                    delete = {
                                                        manageViewModel.deleteOpenImage(it)
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
                            EasyToast(toastState)
                        }
                        1 -> {
                            Box(modifier = Modifier.fillMaxSize().padding(10.dp)){
                                LaunchedEffect(Unit){
                                    manageViewModel.openImageAdd
                                        .filter {
                                            it is NetworkResult.Success<String>
                                        }.collect{
                                            imageByteArray.value = null
                                        }
                                }
                                val toastState = rememberToastState()
                                toastState.toastBindNetworkResult(manageViewModel.openImageDelete.collectAsState())
                                manageViewModel.openImageAdd.collectAsState().CollectWithContent(
                                    content = {
                                        imageByteArray.value?: Icon(
                                            painter = painterResource(MR.images.image),
                                            "",
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clickable {
                                                    imagePicker.pickImage()
                                                }
                                        )
                                        imageByteArray.value?.let {
                                            Column (
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ){
                                                Image(
                                                    bitmap = it.asImageBitmap(),
                                                    "",
                                                    modifier = Modifier
                                                        .fillMaxWidth(0.8f)
                                                        .aspectRatio(0.56f),
                                                    contentScale = ContentScale.FillBounds
                                                )
                                                Row (
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .wrapContentHeight()
                                                        .padding(10.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(20.dp,Alignment.CenterHorizontally)
                                                ){
                                                    Button(
                                                        onClick = {
                                                            imagePicker.pickImage()
                                                        }
                                                    ){
                                                        Text("重选")
                                                    }
                                                    Button(
                                                        onClick = {
                                                            imageByteArray.value?.let {
                                                                manageViewModel.addOpenImage(it)
                                                            }
                                                        },
                                                        modifier = Modifier,
                                                        contentPadding = PaddingValues(horizontal = 40.dp)
                                                    ){
                                                        Text("添加")
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    loading = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier
                                                    .align(Alignment.Center)
                                            )
                                        }
                                    }
                                )
                                EasyToast(toastState)
                            }
                        }
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
    refresh:()->Unit,
    delete:()->Unit
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
                    delete.invoke()
                }
            ){
                Text("删除该开屏页")
            }
        }
    }
}