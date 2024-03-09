package di

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.navigator.Navigator
import com.liftric.kvault.KVault
import config.BaseUrlConfig
import configureForPlatform
import initStore
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.pipeline.PipelinePhase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import org.koin.core.module.Module
import org.koin.dsl.module
import repository.FeedbackRepository
import repository.LoginRepository
import repository.ManageRepository
import repository.ModifierInformationRepository
import repository.NewRepository
import repository.PersonRepository
import repository.PostRepository
import repository.ReportRepository
import repository.RibbonRepository
import repository.SplashRepository
import repository.WeatherRepository
import ui.compose.Action.ActionViewModel
import ui.compose.Authentication.AuthenticationViewModel
import ui.compose.Feedback.FeedBackViewModel
import ui.compose.Manage.ManageViewModel
import ui.compose.ModifierInformation.ModifierInformationViewModel
import ui.compose.Person.PersonViewModel
import ui.compose.Post.PostDetailViewModel
import ui.compose.Post.PostListViewModel
import ui.compose.Release.ReleasePageViewModel
import ui.compose.Report.ReportViewModel
import ui.compose.SplashPage.SplashPageViewModel
import ui.compose.Weather.WeatherViewModel
import ui.root.RootAction
import ui.setting.Setting
import util.compose.Toast
import viewModelDefinition

class LoginClient(
    val client : HttpClient = HttpClient{
        install(ContentNegotiation) {
            json()
        }
        install(
            DefaultRequest
        ){
            url(BaseUrlConfig.BaseUrl)
        }
        install(Logging)
        install(HttpCookies){}
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        configure()
    }
)

class ShareClient(
    val client : HttpClient = HttpClient{
        install(ContentNegotiation) {
            json()
        }
        install(Logging)
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        configure()
    }
)

class WebClient(
    val client : HttpClient = HttpClient{
        install(ContentNegotiation) {
            json()
        }
        install(Logging)
        install(HttpRedirect) {
            checkHttpMethod = false
        }
        configure()
    }
)

class TopBarState(){
    val itemForSelect = mutableStateOf<List<SelectItem>?>(null)
    val expanded = mutableStateOf(false)
    val itemForSelectShow = derivedStateOf {
        itemForSelect.value != null
    }
    fun registerItemForSelect(list:List<SelectItem>?){
        itemForSelect.value = list
    }
    val title = mutableStateOf<String?>(null)

}

data class SelectItem(
    val text : String,
    val click : ()->Unit,
)

data class BackItem(
    val label: String,
    val click: () -> Unit
)

class SystemAction(
    val onBack :() -> Unit,
    val onFinish: () -> Unit
)

fun appModule(
    rootAction: RootAction,
    systemAction: SystemAction,
    navigator: Navigator
) = module {
    single {
        rootAction
    }
    single {
        Setting(get())
    }
    single {
        systemAction
    }
    single {
        navigator
    }
    single {
        val client = HttpClient{
            install(ContentNegotiation) {
                json()
            }
            headers {
                val kVault = get<KVault>()
                val token : String? = kVault.string(forKey = "token")
                token?.let {
                    append("Authorization",token)
                }
            }
            install(
                DefaultRequest
            ){
                val kVault = get<KVault>()
                val token : String? = kVault.string(forKey = "token")
                token?.let {
                    headers.append("Authorization",token)
                }
                url(BaseUrlConfig.BaseUrl)
            }
            install(Logging)
            install(HttpCookies){}
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            configure()
        }
        val authPhase = PipelinePhase("Auth")
        client.receivePipeline.insertPhaseBefore(HttpReceivePipeline.Before,authPhase)
        client.receivePipeline.intercept(authPhase){
            if(it.status.value == 555){
                val kVault = get<KVault>()
                kVault.clear()
                get<RootAction>().reLogin()
            }
            if(it.status.value == 556){
                val kVault = get<KVault>()
                kVault.clear()
                get<RootAction>().reLogin()
            }
            if(it.status.value == 557){
                get<RootAction>().popManage()
            }
        }
        return@single client
    }
    single {
        TopBarState()
    }
    repositoryList()
    viewModel()
    single {
        initStore()
    }
    single {
        val kVault = get<KVault>()
    }
    single {
        LoginClient()
    }
    single {
        ShareClient()
    }
    single {
        WebClient()
    }
    single {
        val scope = CoroutineScope(Job())
        return@single Toast(scope)
    }
}
fun Module.repositoryList(){
    single {
        SplashRepository( get() )
    }
    single {
        LoginRepository( get() )
    }
    single {
        NewRepository(get())
    }
    single {
        PersonRepository(get())
    }
    single {
        PostRepository(get())
    }
    single {
        FeedbackRepository(get())
    }
    single {
        ModifierInformationRepository(get())
    }
    single {
        WeatherRepository(get())
    }
    single {
        ReportRepository(get())
    }
    single {
        ManageRepository(get())
    }
    single {
        RibbonRepository(get())
    }
}
fun Module.viewModel(){
    viewModelDefinition {
        AuthenticationViewModel( get(),get(),get())
    }
    single {
        ActionViewModel( get(),get())
    }
    single {
        SplashPageViewModel(get(),get())
    }
    single {
        PostListViewModel(get(),get(),get(),get())
    }
    single {
        FeedBackViewModel(get())
    }
    single {
        ReportViewModel(get())
    }
    single {
        PostDetailViewModel(get(),get(),get())
    }
    single {
        ManageViewModel(get(),get())
    }
    viewModelDefinition {
        PersonViewModel(get(),get())
    }
    viewModelDefinition {
        WeatherViewModel(get())
    }
    viewModelDefinition {
        ReleasePageViewModel(get())
    }
    viewModelDefinition {
        ModifierInformationViewModel(get())
    }
}
fun HttpClientConfig<*>.configure() {
    configureForPlatform()
}