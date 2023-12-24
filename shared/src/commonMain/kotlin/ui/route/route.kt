package ui.route

import BackHandler
import ComposeSetting
import MainViewState
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    route:RouteState
){
    val toast = koinInject<Toast>()
    val mainViewState = koinInject<MainViewState>()
    Column{
        Crossfade(
            mainViewState.showOrNot.value,
        ){
            if(it){
                Row {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowLeft,
                        null,
                        modifier = Modifier
                            .size(45.dp)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .clickable {
                                mainViewState.showBackButton.lastOrNull()?.invoke()
                            },
                    )
                }
            }
        }

        Box(
            modifier = modifier
        ) {
            Crossfade(
                modifier = Modifier
                    .fillMaxSize(),
                targetState = route.currentPage.value
            ) {
                it?.let {
                    BackHandler(isEnabled = it.canBack) {
                        route.back()
                    }
                    it.content.invoke()
                }
            }

        }
    }
    EasyToast(toast)
}

interface Route{

    val route: String
    val content : @Composable () -> Unit
    val canBack : Boolean

    class RouteNewsDetail(
        val id: String,
        override val route: String,
        override val content : @Composable () -> Unit = {
//            NewsDetail()
        },
        override val canBack: Boolean = true
    ): Route

    class SchoolMap(
        override val route: String = "schoolMap",
        override val content: @Composable () -> Unit = {
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
        override val route: String = "modifer",
        override val content: @Composable (  ) -> Unit = {
            ModifierInformationScreen(userId = userId, userData = userData)
        },
        override val canBack: Boolean = true
    ) : Route

    class OwnWebView(
        val start:String,
        override val route: String = "webview",
        override val content: @Composable () -> Unit = {
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
        override val route: String = "weather",
        override val content: @Composable () -> Unit = {
            WeatherScreen()
        },
        override val canBack: Boolean = true
    ) : Route

    class Main (
        val id: String ,
        override val route: String = "Main",
        override val content: @Composable (  ) -> Unit = {
            MainScreen()
        },
        override val canBack: Boolean = false
    ) : Route

    class ReleasePage (
        override val route: String = "ReleasePage",
        override val content: @Composable (  ) -> Unit = {
            ReleasePageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp)
            )
        },
        override val canBack: Boolean = true
    ) : Route

    class Person (
        override val route: String = "person",
        id: String? = null,
        override val content: @Composable () -> Unit = {
           PersonScreen(
               id
           )
        },
        override val canBack: Boolean = true
    ) : Route

    class Massage private constructor(
        override val route: String,
        override val content: @Composable ( ) -> Unit = {
            MassageScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .padding( all = 10.dp )
            )
        },
        override val canBack: Boolean = true
    ):Route

    class LoginWithRegister(
        override val route: String = "loginWithRegister",
        override val content: @Composable () -> Unit = {
            Assembly(Modifier.fillMaxSize())
        },
        override val canBack: Boolean = false
    ):Route

    class Splash(
        override val route: String = "Splash",
        override val content: @Composable () -> Unit = {
            SplashPage(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            )
        },
        override val canBack: Boolean = false
    ):Route

    class QRCode(
        override val route: String = "QRCode",
        override val content: @Composable () -> Unit = {
            QRCodeScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Feedback(
        override val route: String = "QRCode",
        override val content: @Composable () -> Unit = {
            FeedbackScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Test(
        override val route: String = "Test",
        val reportType: ReportType,
        override val content: @Composable () -> Unit = {
            ReportScreen(
                type = reportType
            )
        },
        override val canBack: Boolean = true
    ):Route

    class Report(
        override val route: String = "report",
        private val reportType: ReportType,
        override val content: @Composable () -> Unit = {
            ReportScreen(
                type = reportType
            )
        },
        override val canBack: Boolean = true
    ):Route

    class AboutUs(
        override val route: String = "aboutUs",
        override val content: @Composable () -> Unit = {
            AboutUsScreen(
                modifier = Modifier
                    .fillMaxSize()
            )
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








