package ui.compose.PERSON

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import config.BaseUrlConfig
import data.Person.UserData
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import ui.util.compose.EasyToast
import ui.util.compose.rememberToastState
import ui.util.compose.shimmerLoadingAnimation
import ui.util.network.CollectWithContent
import ui.util.network.NetworkResult
import ui.util.network.logicWithType


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PersonScreen(
    modifier: Modifier = Modifier,
    viewModel: PersonViewModel = koinInject()
){
    val userDataState = viewModel.userData.collectAsState()
    LaunchedEffect(Unit){
        viewModel.getUserData()
    }

    val toast = rememberToastState()
    LaunchedEffect(userDataState.value,userDataState.value.key){
        userDataState.value.logicWithType(
            success = null,
            error = {
                toast.addToast(it.message.toString(), Color.Red)
            }
        )
    }
    Box(modifier = Modifier.fillMaxSize()){
        Column (
            modifier = Modifier
                .fillMaxSize()
        ){
            viewModel.userData.collectAsState().CollectWithContent(
                success = {
                    Column{
                        KamelImage(
                            resource = asyncPainterResource("https://picx.zhimg.com/80/v2-cee6c1a92831cd1566e4c5f9dd4a35f4_720w.webp?source=1def8aca"),
                            null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .shimmerLoadingAnimation(
                                    colorList = listOf(
                                        Color.Black.copy(alpha = 0.1f),
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.2f),
                                        Color.Black.copy(alpha = 0.1f),
                                    )
                                ),
                            contentScale = ContentScale.FillBounds
                        )
                        Row {
                            PersonalInformationInPerson(
                                modifier = Modifier
                                    .offset(y = (-25).dp)
                                    .wrapContentHeight()
                                    .weight(1f)
                                    .padding(start = 10.dp),
                                viewModel.userData.collectAsState()
                            )
                            Button(
                                onClick = {
                                    it.data?.let { it1 ->
                                        viewModel.navigateToModifierInformation(
                                            userId = it1.Id, userData = it
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .padding(horizontal = 5.dp)
                            ) {
                                Text(
                                    "编辑个人信息",
                                )
                            }
                        }
                    }
                }
            )
            val items = listOf("发布","动态","身份")
            val selectedItem = remember {
                mutableStateOf(0)
            }
            val pageState = rememberPagerState {
                items.size
            }
            LaunchedEffect(selectedItem.value){
                pageState.animateScrollToPage(selectedItem.value)
            }
            TabRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                selectedTabIndex = selectedItem.value,
                tabs = {
                    items.forEachIndexed { index, item ->
                        Tab(
                            text = {
                                 Text(item)
                            },
                            selected = selectedItem.value == index,
                            onClick = { selectedItem.value = index },
                            selectedContentColor = Color.Magenta
                        )
                    }
                }
            )
        }
        EasyToast(toast = toast)
    }
}

//@Composable
//fun PersonHeader()

@Composable
fun PersonalInformationInPerson(
    modifier: Modifier = Modifier,
    userData: State<NetworkResult<UserData>>
){
    Column(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .height(50.dp)
                .aspectRatio(1f)
                .clip(CircleShape),
        ){
            userData.value.let {
                when(it){
                    is NetworkResult.Success<UserData> -> {
                        KamelImage(
                            resource = asyncPainterResource("${BaseUrlConfig.BaseUrl}/static/userAvatar/${it.data.data!!.avatar}"),
                            null,
                            modifier = Modifier
                                .fillMaxSize(),
                            contentScale = ContentScale . FillBounds
                        )
                    }
                    else -> Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .shimmerLoadingAnimation()
                    )
                }
            }


        }
        Text(
            text = when(userData.value){
                is NetworkResult.Success<UserData> -> (userData.value as NetworkResult.Success<UserData>).data.data!!.username
                is NetworkResult.Error<UserData> -> "加载失败"
                is NetworkResult.UnSend<UserData> -> "加载中"
                is NetworkResult.Loading<UserData> -> "加载中"
                else -> "加载失败"
            },
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding( top = 10.dp )
        )
        Text(
            text = when(userData.value){
                is NetworkResult.Success<UserData> -> (userData.value as NetworkResult.Success<UserData>).data.data!!.email
                is NetworkResult.Error<UserData> -> "加载失败"
                is NetworkResult.UnSend<UserData> -> "加载中"
                is NetworkResult.Loading<UserData> -> "加载中"
                else -> "加载失败"
            },
            fontSize = 10.sp
        )
        Text(
            text = when(userData.value){
                is NetworkResult.Success<UserData> -> "🗺️ ${(userData.value as NetworkResult.Success<UserData>).data.data!!.location}"
                is NetworkResult.Error<UserData> -> "加载失败"
                is NetworkResult.UnSend<UserData> -> "加载中"
                is NetworkResult.Loading<UserData> -> "加载中"
                else -> "加载失败"
            },
            fontSize = 10.sp
        )
    }
}

fun State<NetworkResult<UserData>>.string(
    success:String = "加载失败",
    error:String = "加载失败",
    unSend:String = "加载失败",
    loading:String = "加载失败"
):String{
    return when(this.value){
        is NetworkResult.Success-> success
        is NetworkResult.Error -> error
        is NetworkResult.UnSend -> unSend
        is NetworkResult.Loading -> loading
        else -> "加载失败"
    }
}
