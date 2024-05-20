package ui.root

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import config.BaseUrlConfig
import data.person.UserData.Data
import di.SystemAction
import di.appModule
import initStore
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.compose.AboutUs.AboutUsVoyagerScreen
import ui.compose.Authentication.LoginAndRegisterVoyagerScreen
import ui.compose.EmptyHouse.EmptyHouseVoyagerScreen
import ui.compose.Feedback.FeedbackVoyagerScreen
import ui.compose.Log.LogVoyagerScreen
import ui.compose.Main.MainVoyagerScreen
import ui.compose.Manage.ManageVoyagerScreen
import ui.compose.ModifierInformation.ModifierInformationVoyagerScreen
import ui.compose.QRCode.QRCodeVoyagerScreen
import ui.compose.Release.ReleaseRouteVoyagerScreen
import ui.compose.Report.ReportType
import ui.compose.Report.ReportVoyagerScreen
import ui.compose.Setting.SettingVoyagerScreen
import ui.compose.SplashPage.SplashPageVoyagerScreen
import ui.compose.Weather.WeatherVoyagerScreen
import ui.compose.Webview.WebViewVoyagerScreen
import util.compose.EasyToast
import util.compose.FuTalkTheme
import util.compose.SettingTransitions

/**
 * Root action
 * 用于第一层级的界面切换
 * @constructor Create empty Root action
 */
interface RootAction{
    fun reLogin()
    fun finishLogin()
    fun navigateFormSplashToMainPage()
    fun navigateFormSplashToLoginAndRegister()
    fun navigateFromActionToFeedback()
    fun navigateFromActionToQRCodeScreen()
    fun navigateFromActionToAboutUs()
    fun navigateFromAnywhereToManage()
    fun navigateFromAnywhereToWeather()
    fun navigateFormLoginToMain()
    fun navigateFormAnywhereToRelease(initLabelList: List<String>)
    fun navigateFormPostToReport(type: ReportType)
    fun navigateFromAnywhereToWebView(url:String)
    fun navigateFormAnywhereToSetting()
    fun navigateFormAnywhereToMain()
    fun navigateFormAnywhereToLog()
    fun navigateFormAnywhereToInformationModifier(userData: Data)
    fun popManage()
    fun navigateToScreen(screen: Screen)
}


@Composable
fun getRootAction(): RootAction {
    return koinInject<RootAction>()
}

/**
 * 解析token
 * @property target String
 * @property verifyFunction Function1<String, Boolean> 验证是否符合
 * @property toActionString Function1<String, String> 解析成功的行为
 * @constructor
 */
enum class TokeJump(
    val target:String,
    val verifyFunction:(String)->Boolean,
    val toActionString:(String)->String
){
    /**
     * Post
     * 转到特定的post
     * @constructor Create empty Post
     */
    Post(target = "POST", verifyFunction = {
        it.toIntOrNull() == null
    }, toActionString = {
        "POST-${it}"
    }),

    /**
     * Web
     * 转到特定的web网页
     * @constructor Create empty Web
     */
    WEB(target = "WEB", verifyFunction = {
        val regexString = ""
        val regex = Regex(regexString)
        regex.matches(it)
    }, toActionString = {
        "WEB-${it}"
    }),

    /**
     * Null
     * 无
     * @constructor Create empty Null
     */
    Null(target = "NULL", verifyFunction = {
        it == "null"
    }, toActionString = {
        "NULL"
    })
}
fun tokenJump(
    tokenForParse:String,
    fail:CoroutineScope.()->Unit,
    rootAction:RootAction,
    scope: CoroutineScope
){
    val result = tokenForParse.split("_FuTalk.Action_")
    if( result.size != 2 ){
        fail.invoke(scope)
        return
    }
    result.let {
        val action = it[1]
        when(it[0]){
            "WEBVIEW" -> {}
        }
    }
}

/**
 * 根ui
 * @param systemAction SystemAction
 */
@Composable
fun RootUi(
    systemAction: SystemAction
){
    Navigator(SplashPageVoyagerScreen()){ navigate ->
        KoinApplication(application = {
            modules(
                appModule(
                    object : RootAction{
                        override fun finishLogin() {
                            navigate.replaceAll(ManageVoyagerScreen())
                        }

                        override fun reLogin() {
                            initStore().clear()
                            navigate.replaceAll(LoginAndRegisterVoyagerScreen)
                        }

                        override fun navigateFormSplashToMainPage() {
                            navigate.replaceAll(MainVoyagerScreen())
                        }

                        override fun navigateFormSplashToLoginAndRegister() {
                            navigate.replaceAll(LoginAndRegisterVoyagerScreen)
                        }

                        override fun navigateFromActionToFeedback() {
                            navigate.push(FeedbackVoyagerScreen())
                        }

                        override fun navigateFromActionToQRCodeScreen() {
                            navigate.push(QRCodeVoyagerScreen())
                        }

                        override fun navigateFromActionToAboutUs() {
                            navigate.push(AboutUsVoyagerScreen())
                        }

                        override fun navigateFromAnywhereToManage() {
                            navigate.push(ManageVoyagerScreen())
                        }

                        override fun navigateFromAnywhereToWeather() {
                            navigate.push(WeatherVoyagerScreen())
                        }

                        override fun navigateFormLoginToMain() {
                            navigate.replaceAll(MainVoyagerScreen())
                        }
                        override fun navigateFormAnywhereToRelease(initLabelList: List<String>){
                            navigate.push(ReleaseRouteVoyagerScreen(listOf()))
                        }

                        override fun navigateFormPostToReport(type: ReportType) {
                            navigate.push(ReportVoyagerScreen(type))
                        }

                        override fun navigateFromAnywhereToWebView(url: String) {
                            navigate.push(WebViewVoyagerScreen(url))
                        }

                        override fun navigateFormAnywhereToSetting() {
                            navigate.push(SettingVoyagerScreen())
                        }

                        override fun navigateFormAnywhereToMain() {
                            navigate.push(MainVoyagerScreen())
                        }

                        override fun navigateFormAnywhereToInformationModifier(userData: Data) {
                            navigate.push(ModifierInformationVoyagerScreen(userData = userData))
                        }

                        override fun popManage() {
                            if (navigate.lastItem is ManageVoyagerScreen && navigate.canPop){
                                navigate.pop()
                            }
                        }

                        override fun navigateFormAnywhereToLog() {
                            navigate.push(LogVoyagerScreen())
                        }

                        override fun navigateToScreen(screen: Screen) {
                            navigate.push(screen)
                        }
                    },
                    systemAction = systemAction,
                    navigator = navigate,
                )
            )
        }) {
            Box(modifier = Modifier.fillMaxSize()){
                if(BaseUrlConfig.isDebug){
                    IconButton(
                        onClick = {
                            navigate.push(EmptyHouseVoyagerScreen())
                        },
                        modifier = Modifier
                            .navigationBarsPadding()
                            .systemBarsPadding()
                            .offset(x = 10.dp,y = (-10).dp)
                    ){
                        Icon(Icons.Default.Add,null)
                    }
                }
                FuTalkTheme {
                    SettingTransitions(navigate)
                }
                EasyToast(toast = koinInject())
            }
        }
    }
}
