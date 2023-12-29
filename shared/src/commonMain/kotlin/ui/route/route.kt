package ui.route

import BackHandler
import ComposeSetting
import TopBarState
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.Person.UserData.Data
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.compose.koinInject
import ui.compose.AboutUs.AboutUsScreen
import ui.compose.Authentication.Assembly
import ui.compose.Feedback.FeedbackScreen
import ui.compose.Main.MainScreen
import ui.compose.Manage.ManageScreen
import ui.compose.Massage.MassageScreen
import ui.compose.ModifierInformation.ModifierInformationScreen
import ui.compose.Person.PersonScreen
import ui.compose.QRCode.QRCodeScreen
import ui.compose.Release.ReleasePageScreen
import ui.compose.Report.ReportScreen
import ui.compose.Report.ReportType
import ui.compose.SchoolMap.SchoolMapScreen
import ui.compose.SplashPage.SplashPage
import ui.compose.Weather.WeatherScreen
import ui.compose.Webview.OwnWebViewScreen
import ui.util.compose.EasyToast
import ui.util.compose.Toast

@Composable
fun RouteHost(
    modifier: Modifier = Modifier.ComposeSetting(),
    routeState:RouteState
){
    val toast = koinInject<Toast>()
    val topBarState = koinInject<TopBarState>()
    Box(
        modifier = modifier
    ) {
        Crossfade(
            modifier = Modifier
                .fillMaxSize(),
            targetState = routeState.currentPage.value
        ) {
            it?.let { route ->
                val routeViewState = remember {
                    mutableStateOf(RouteViewState())
                }
                Column{
                    Crossfade( routeState.canBack.value || topBarState.itemForSelectShow.value ){
                        if (it) {
                            Row ( verticalAlignment = Alignment.CenterVertically ){
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowLeft,
                                    null,
                                    modifier = Modifier
                                        .size(45.dp)
                                        .padding(4.dp)
                                        .clip(CircleShape)
                                        .clickable {
                                            routeState.back()
                                        },
                                )
                                Box( modifier = Modifier.wrapContentHeight().weight(1f) ){
                                    Crossfade(
                                        route.route
                                    ){
                                        Text(
                                            modifier = Modifier.fillMaxWidth(),
                                            text = it,
                                            textAlign = TextAlign.Start,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Crossfade( topBarState.itemForSelectShow.value ){
                                    if(it){
                                        IconButton(
                                            onClick = { topBarState.expanded.value = true },
                                        ){
                                            Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                                            DropdownMenu(
                                                expanded = topBarState.expanded.value,
                                                onDismissRequest = {
                                                    topBarState.expanded.value = false
                                                }
                                            ){
                                                topBarState.itemForSelect.value?.let {
                                                    it.forEach {
                                                        DropdownMenuItem(
                                                            onClick = {
                                                                it.click.invoke()
                                                            }
                                                        ){
                                                            Text(text = it.text)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ){
                        BackHandler(isEnabled = it.canBack) {
                            routeState.back()
                        }
                        it.content.invoke(routeViewState.value)
                    }
                }
            }
        }
    }
    EasyToast(toast)
}

interface Route {
    val route: String
    val content : @Composable (RouteViewState) -> Unit
    val canBack : Boolean
    class RouteNewsDetail(
        val id: String,
        override val route: String,
        override val content : @Composable (RouteViewState) -> Unit = {
//            NewsDetail()
        },
        override val canBack: Boolean = true
    ): Route

    class SchoolMap(
        override val route: String = "校园地图",
        override val content: @Composable (RouteViewState) -> Unit = {
            SchoolMapScreen(
                modifier = Modifier,
                url = "https://pic1.zhimg.com/80/v2-ae48979cee947ee6cae729ef14bc144b_1440w.webp?source=2c26e567",
                onClick = { -> }
            )
        },
        override val canBack: Boolean = true
    ):Route

    class ModifierInformation(
        val userId:Int,
        val userData: Data,
        override val route: String = "修改个人信息",
        override val content: @Composable (RouteViewState) -> Unit = {
            ModifierInformationScreen(userId = userId, userData = userData)
        },
        override val canBack: Boolean = true
    ) : Route

    class OwnWebView(
        val start:String,
        override val route: String = "",
        override val content: @Composable (RouteViewState) -> Unit = {
            val routeState = koinInject<RouteState>()
            OwnWebViewScreen(
                back = {
                    routeState.back()
                },
                start = start
            )
        },
        override val canBack: Boolean = true
    ) : Route

    class Weather(
        override val route: String = "天气",
        override val content: @Composable (RouteViewState) -> Unit = {
            WeatherScreen()
        },
        override val canBack: Boolean = true
    ) : Route

    class Main (
        override val route: String = "主页",
        override val content: @Composable (RouteViewState) -> Unit = {
            MainScreen()
        },
        override val canBack: Boolean = false
    ) : Route

    class ReleasePage (
        override val route: String = "发布页",
        override val content: @Composable (RouteViewState) -> Unit = {
            ReleasePageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
        },
        override val canBack: Boolean = true
    ) : Route

    class Person (
        override val route: String = "个人页",
        id: String? = null,
        override val content: @Composable (RouteViewState) -> Unit = {
           PersonScreen(
               id
           )
        },
        override val canBack: Boolean = true
    ) : Route

    class Massage private constructor(
        override val route: String = "信息",
        override val content: @Composable (RouteViewState) -> Unit = {
            MassageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding( all = 10.dp )
            )
        },
        override val canBack: Boolean = true
    ):Route

    class LoginWithRegister(
        override val route: String = "登录注册",
        override val content: @Composable (RouteViewState) -> Unit = {
            Assembly(Modifier.fillMaxSize())
        },
        override val canBack: Boolean = false
    ):Route

    class Splash(
        override val route: String = "开屏页",
        override val content: @Composable (RouteViewState) -> Unit = {
            SplashPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        },
        override val canBack: Boolean = false
    ):Route

    class QRCode(
        override val route: String = "二维码生成",
        override val content: @Composable (RouteViewState) -> Unit = {
            QRCodeScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Feedback(
        override val route: String = "反馈",
        override val content: @Composable (RouteViewState) -> Unit = {
            FeedbackScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Test(
        override val route: String = "测试",
        val reportType: ReportType,
        override val content: @Composable (RouteViewState) -> Unit = {
            ReportScreen(
                type = reportType
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Report(
        override val route: String = "举报",
        private val reportType: ReportType,
        override val content: @Composable (RouteViewState) -> Unit = {
            ReportScreen(
                type = reportType
            )
        },
        override val canBack: Boolean = true
    ):Route

    class AboutUs(
        override val route: String = "关于我们",
        override val content: @Composable (RouteViewState) -> Unit = {
            AboutUsScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Manage(
        override val route: String = "管理社区",
        override val content: @Composable (RouteViewState) -> Unit = {
                ManageScreen()
        },
        override val canBack: Boolean = true
    ):Route

}

class RouteState(start:Route){
    private val route = mutableStateListOf<Route>(start)
    val currentPage = derivedStateOf {
        if(route.isEmpty()){
            return@derivedStateOf null
        }
        route.last()
    }
    val canBack = derivedStateOf {
        if(route.isEmpty()){
            return@derivedStateOf false
        }
        route.size != 1 && route.last() !is Route.LoginWithRegister
    }

    private val scope = CoroutineScope(Job())
    private val mutex = Mutex()
    fun back(){
        if(!mutex.isLocked){
            scope.launch(Dispatchers.Default) {
                mutex.withLock {
                    route.removeLast()
                }
            }
        }
    }
    fun navigateWithPop(newRoute: Route){
        route.removeLastOrNull()
        route.add(newRoute)
    }
    fun navigateWithoutPop(newRoute: Route){
        route.add(newRoute)
    }

    fun reLogin(){
        route.clear()
        route.add(Route.LoginWithRegister())
    }
}

class RouteViewState(
    val isShow: MutableState<Boolean> = mutableStateOf(false)
)








