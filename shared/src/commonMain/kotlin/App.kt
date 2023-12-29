
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.liftric.kvault.KVault
import config.BaseUrlConfig
import dev.icerock.moko.mvvm.viewmodel.ViewModel
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
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import repository.FeedbackRepository
import repository.LoginRepository
import repository.ModifierInformationRepository
import repository.NewRepository
import repository.PersonRepository
import repository.PostRepository
import repository.SplashRepository
import repository.WeatherRepository
import ui.compose.Authentication.AuthenticationViewModel
import ui.compose.Feedback.FeedBackViewModel
import ui.compose.ModifierInformation.ModifierInformationViewModel
import ui.compose.Person.PersonViewModel
import ui.compose.Post.NewViewModel
import ui.compose.Release.ReleasePageViewModel
import ui.compose.Ribbon.RibbonViewModel
import ui.compose.SplashPage.SplashPageViewModel
import ui.compose.Weather.WeatherViewModel
import ui.route.Route
import ui.route.RouteHost
import ui.route.RouteState
import ui.util.compose.FuTalkTheme
import ui.util.compose.Toast


@Composable
fun App(
    onBack: () -> Unit = {},
    finish :() ->Unit = {}
){
    KoinApplication(application = {
        modules(appModule(
            onBack,
            finish
        ))
    }) {
        val route = koinInject<RouteState>()
        FuTalkTheme{
            RouteHost(
                modifier = Modifier.fillMaxSize(),
                routeState = route
            )
        }
    }
}

expect fun getPlatformName(): String

@Composable
fun BackHandler(isEnabled: Boolean, onBack: ()-> Unit){
    BackHandlerWithPlatform(isEnabled,onBack)
}

@Composable
expect fun BackHandlerWithPlatform(isEnabled: Boolean, onBack: ()-> Unit)

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>

expect fun ByteArray.asImageBitmap(): ImageBitmap

fun appModule(
    onBack: () -> Unit,
    onFinish :() ->Unit
) = module {
    single {
        SystemAction(onBack,onFinish)
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
            install(HttpCookies){

            }
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            configure()
        }
        val authPhase = PipelinePhase("Auth")
        client.receivePipeline.insertPhaseBefore(HttpReceivePipeline.Before,authPhase)
        client.receivePipeline.intercept(authPhase){
            if(it.status.value in 555..559){
                println(it.status)
                val kVault = get<KVault>()
                kVault.clear()
                get<RouteState>().reLogin()
            }
        }
        return@single client
    }
    single {
        TopBarState( get() )
    }
    repositoryList()
    viewModel()
    single {
        initStore()
    }
    single {
        val kVault = get<KVault>()
        val token : String? = kVault.string(forKey = "token")
//        mutableStateListOf<Route>(
//            if( token == null ) Route.LoginWithRegister() else Route.Splash()
//        )
        RouteState( if( token == null ) Route.LoginWithRegister() else Route.Splash())
    }
    single {
        LoginClient()
    }
    single {
        ShareClient()
    }
    single {
        val scope = CoroutineScope(Job())
        return@single Toast(scope)
    }
}

fun HttpClientConfig<*>.configure() {
    configureForPlatform()
}

internal expect fun HttpClientConfig<*>.configureForPlatform()

expect fun initStore(): KVault

expect @Composable fun Modifier.ComposeSetting():Modifier

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
}

fun Module.viewModel(){
    viewModelDefinition {
        AuthenticationViewModel( get(),get(),get() )
    }
    viewModelDefinition {
        RibbonViewModel( get(), get()  )
    }
    single {
        SplashPageViewModel(get(),get(),get())
    }
    single {
        NewViewModel(get(),get(),get(),get())
    }
    single {
        FeedBackViewModel(get())
    }
    viewModelDefinition {
        PersonViewModel(get(),get(),get())
    }
    viewModelDefinition {
        WeatherViewModel(get())
    }
    viewModelDefinition {
        ReleasePageViewModel(get(),get())
    }
    viewModelDefinition {
        ModifierInformationViewModel(get())
    }
}

expect class ImagePickerFactory(context: PlatformContext) {
    @Composable
    fun createPicker(): ImagePicker
}

expect class ImagePicker {
    @Composable
    fun registerPicker(onImagePicked: (ByteArray) -> Unit)
    fun pickImage()
}

@Composable
expect fun rememberBitmapFromBytes(bytes: ByteArray?): ImageBitmap?

expect class PlatformContext

@Composable
expect fun getPlatformContext(): PlatformContext

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
        install(HttpCookies){

        }
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

class TopBarState(val routeState : RouteState){
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