package ui.root

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.ScaleTransition
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import com.bumble.appyx.utils.multiplatform.RawValue
import data.person.UserData.Data
import di.SystemAction
import di.appModule
import initStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import ui.compose.AboutUs.AboutUsVoyagerScreen
import ui.compose.Authentication.LoginAndRegisterVoyagerScreen
import ui.compose.Feedback.FeedbackVoyagerScreen
import ui.compose.Main.Main
import ui.compose.Manage.ManageVoyagerScreen
import ui.compose.QRCode.QRCodeVoyagerScreen
import ui.compose.Release.ReleaseRouteVoyagerScreen
import ui.compose.Report.ReportType
import ui.compose.Report.ReportVoyagerScreen
import ui.compose.SplashPage.SplashPageVoyagerScreen
import ui.compose.Weather.WeatherVoyagerScreen
import ui.compose.Webview.WebViewVoyagerScreen
import util.compose.FuTalkTheme


sealed class RootTarget : Parcelable {
    @Parcelize
    data object Main : RootTarget()

    @Parcelize
    data object AboutUs : RootTarget()

    @Parcelize
    data object Weather : RootTarget()

    @Parcelize
    data object Massage : RootTarget()

    @Parcelize
    data object Authentication : RootTarget()

    @Parcelize
    data object SplashPage : RootTarget()

    @Parcelize
    class ModifierInformation(val userData: @RawValue Data) : RootTarget()

    @Parcelize
    class Person(
        val userId:String?
    ) : RootTarget()

    @Parcelize
    data object QRCode : RootTarget()

    @Parcelize
    data object Release : RootTarget()

    @Parcelize
    class Report(
        val type : @RawValue ReportType,
    ) : RootTarget()

    @Parcelize
    data object Ribbon : RootTarget()

    @Parcelize
    data object Feedback : RootTarget()

    @Parcelize
    data object Manage : RootTarget()

    @Parcelize
    class WebView(
        val url :String
    ) : RootTarget()
}




interface RootAction{
    fun navigateToNewTarget(rootTarget : RootTarget)
    fun replaceNewTarget(rootTarget : RootTarget)
    fun navigateBack()
    fun canBack():StateFlow<Boolean>
    fun reLogin()
    fun navigateFormSplashToMainPage()
    fun navigateFormSplashToLoginAndRegister()
    fun navigateFromActionToFeedback()
    fun navigateFromActionToQRCodeScreen()
    fun navigateFromActionToAboutUs()
    fun navigateFromActionToManage()
    fun navigateFromActionToWeather()
    fun navigateFormPostToRelease()
    fun navigateFormPostToReport(type: ReportType)
    fun navigateFromAnywhereToWebView(url:String)
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
            "WEBVIEW" -> rootAction.navigateToNewTarget(RootTarget.WebView(action))
        }
    }
}

@Composable
fun RootUi(
    systemAction: SystemAction
){
    FuTalkTheme{
        Navigator(SplashPageVoyagerScreen()){ navigate ->
            KoinApplication(application = {
                modules(appModule(
                    object : RootAction{

                        override fun navigateToNewTarget(rootTarget: RootTarget) {}

                        override fun replaceNewTarget(rootTarget: RootTarget) {}

                        override fun navigateBack() {}

                        override fun canBack() = MutableStateFlow(true)

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

                        override fun navigateFromActionToManage() {
                            navigate.push(ManageVoyagerScreen())
                        }

                        override fun navigateFromActionToWeather() {
                            navigate.push(WeatherVoyagerScreen)
                        }

                        override fun navigateFormPostToRelease(){
                            navigate.push(ReleaseRouteVoyagerScreen())
                        }

                        override fun navigateFormPostToReport(type: ReportType) {
                            navigate.push(ReportVoyagerScreen(type))
                        }

                        override fun navigateFromAnywhereToWebView(url: String) {
                            navigate.push(WebViewVoyagerScreen(url))
                        }
                    },
                    systemAction = systemAction,
                    navigator = navigate
                ))
            }) {
                ScaleTransition(navigate)
            }
        }
    }
}
