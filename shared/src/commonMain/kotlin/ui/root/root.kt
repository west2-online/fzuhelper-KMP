package ui.root

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import data.person.UserData.Data
import di.SystemAction
import di.appModule
import initStore
import kotlinx.coroutines.CoroutineScope
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.compose.AboutUs.AboutUsVoyagerScreen
import ui.compose.Authentication.LoginAndRegisterVoyagerScreen
import ui.compose.Feedback.FeedbackVoyagerScreen
import ui.compose.Main.Main
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
import ui.setting.SettingTransitions
import util.compose.FuTalkTheme


interface RootAction{
    fun reLogin()
    fun navigateFormSplashToMainPage()
    fun navigateFormSplashToLoginAndRegister()
    fun navigateFromActionToFeedback()
    fun navigateFromActionToQRCodeScreen()
    fun navigateFromActionToAboutUs()
    fun navigateFromAnywhereToManage()
    fun navigateFromAnywhereToWeather()
    fun navigateFormAnywhereToRelease()
    fun navigateFormPostToReport(type: ReportType)
    fun navigateFromAnywhereToWebView(url:String)
    fun navigateFormAnywhereToSetting()
    fun navigateFormAnywhereToMain()
    fun navigateFormAnywhereToInformationModifier(userData: Data)
    fun popManage()
}

@Composable
fun getRootAction(): RootAction {
    return koinInject<RootAction>()
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

@Composable
fun RootUi(
    systemAction: SystemAction
){
    Navigator(SplashPageVoyagerScreen()){ navigate ->
        KoinApplication(application = {
            modules(
                appModule(
                    object : RootAction{

                        override fun reLogin() {
                            initStore().clear()
                            navigate.replaceAll(LoginAndRegisterVoyagerScreen)
                        }

                        override fun navigateFormSplashToMainPage() {
                            navigate.replaceAll(Main)
                        }

                        override fun navigateFormSplashToLoginAndRegister() {
                            navigate.replaceAll(LoginAndRegisterVoyagerScreen)
                        }

                        override fun navigateFromActionToFeedback() {
                            navigate.push(FeedbackVoyagerScreen())
                        }

                        override fun navigateFromActionToQRCodeScreen() {
                            navigate.push(QRCodeVoyagerScreen)
                        }

                        override fun navigateFromActionToAboutUs() {
                            navigate.push(AboutUsVoyagerScreen)
                        }

                        override fun navigateFromAnywhereToManage() {
                            navigate.push(ManageVoyagerScreen())
                        }

                        override fun navigateFromAnywhereToWeather() {
                            navigate.push(WeatherVoyagerScreen)
                        }

                        override fun navigateFormAnywhereToRelease(){
                            navigate.push(ReleaseRouteVoyagerScreen())
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
                            navigate.push(Main)
                        }

                        override fun navigateFormAnywhereToInformationModifier(userData: Data) {
                            navigate.push(ModifierInformationVoyagerScreen(userData = userData))
                        }

                        override fun popManage() {
                            if (navigate.lastItem is ManageVoyagerScreen && navigate.canPop){
                                navigate.pop()
                            }
                        }
                    },
                    systemAction = systemAction,
                    navigator = navigate
                )
            )
        }) {
            FuTalkTheme{
                SettingTransitions(navigate)
            }
        }
    }
}
