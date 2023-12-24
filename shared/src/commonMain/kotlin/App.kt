
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.liftric.kvault.KVault
import config.BaseUrlConfig
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
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
import org.example.library.MR
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



@Composable
fun App(){
    KoinApplication(application = {
        modules(appModule())
    }) {
        val route = koinInject<RouteState>()
        val mainViewState = koinInject<MainViewState>()
        FuTalkTheme{
            RouteHost(
                modifier = Modifier.fillMaxSize(),
                route = route
            )
        }
        BackHandlerWithPlatform(mainViewState.showOrNot.value || !route.canBack.value){
//            route.back()
            mainViewState.showBackButton.last().invoke()
        }
    }
}
//fun getMyPluralDesc(quantity: Int): StringDesc {
//    return StringDesc.Plural(MR.plurals.my_string, quantity)
//}
fun getMyString(): StringDesc {
    return StringDesc.Resource(MR.strings.my_string)
}

expect fun getPlatformName(): String


@Composable
fun BackHandler(isEnabled: Boolean, onBack: ()-> Unit){
    if(isEnabled){
        val mainViewState = koinInject<MainViewState>()
        DisposableEffect(Unit){
            mainViewState.showBackButton.add(onBack)
            onDispose {
                mainViewState.showBackButton.removeLast()
            }
        }
    }
}

@Composable
expect fun BackHandlerWithPlatform(isEnabled: Boolean, onBack: ()-> Unit)

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>

expect fun ByteArray.asImageBitmap(): ImageBitmap

fun appModule() = module {
    single {
        val client = HttpClient{
            install(ContentNegotiation) {
                json()
            }
            headers {
                val kVault = get<KVault>()
                val token : String? = kVault.string(forKey = "token")
                println("token in clint data: ${token}")
                println()
                token?.let {
                    append("Authorization",token)
                }
            }
            install(
                DefaultRequest
            ){
                val kVault = get<KVault>()
                val token : String? = kVault.string(forKey = "token")
                println("token in clint data: ${token}")
                println()
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
//            HttpResponseValidator{
//                validateResponse{
//                    val data = it
//                    println("this is data ${data.bodyAsText()}")
//                    it.ensureActive()
//                }
//            }
        }
//        client.sendPipeline.intercept(HttpSendPipeline.State){
//            val kVault = get<KVault>()
//            val token : String? = kVault.string(forKey = "token")
//            context.header("Authorization",token)
//        }
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
        MainViewState()
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
}

fun HttpClientConfig<*>.configure() {
    // TODO: Add common config
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


suspend fun KVault.token(routeState: RouteState,block:suspend (token:String)->Unit) {
    val token = this.string("token")
    if(token == null ){
        routeState.reLogin()
    }
    else{
        block.invoke(token)
    }
}

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

class MainViewState{
    val showBackButton = mutableStateListOf<(()->Unit)>()
    val showOrNot = derivedStateOf {
        showBackButton.size != 0
    }
}

