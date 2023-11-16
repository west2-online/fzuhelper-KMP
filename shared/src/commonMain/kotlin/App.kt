
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.liftric.kvault.KVault
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
import io.ktor.serialization.kotlinx.json.json
import org.example.library.MR
import org.koin.compose.KoinApplication
import org.koin.compose.koinInject
import org.koin.core.definition.Definition
import org.koin.core.definition.KoinDefinition
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.dsl.module
import repository.LoginRepository
import repository.NewRepository
import repository.SplashRepository
import ui.compose.Authentication.AuthenticationViewModel
import ui.compose.New.NewViewModel
import ui.compose.Ribbon.RibbonViewModel
import ui.compose.SplashPage.SplashPageViewModel
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
        FuTalkTheme {
            RouteHost(
                modifier = Modifier.fillMaxSize(),
                route = route
            )
        }
        BackHandler(route.canBack.value){
            route.back()
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
expect fun BackHandler(isEnabled: Boolean, onBack: @Composable ()-> Unit)

expect inline fun <reified T : ViewModel> Module.viewModelDefinition(
    qualifier: Qualifier? = null,
    noinline definition: Definition<T>
): KoinDefinition<T>

expect fun ByteArray.asImageBitmap(): ImageBitmap

fun appModule() = module {
    single {
        HttpClient{
            install(ContentNegotiation) {
                json()
            }
            install(
                DefaultRequest
            ){
                url(get<BaseUrl>().getMainUrl())
            }
            install(Logging)
            install(HttpCookies){

            }
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            configure()
        }
    }
    repositoryList()
    viewModel()
    single {
        initStore()
    }
    single{
        BaseUrl()
    }
    single {
        val kVault = get<KVault>()
        val token : String? = kVault.string(forKey = "token")
//        mutableStateListOf<Route>(
//            if( token == null ) Route.LoginWithRegister() else Route.Splash()
//        )
        RouteState( if( token == null ) Route.LoginWithRegister() else Route.Splash())
    }
}

fun HttpClientConfig<*>.configure() {
    // TODO: Add common config
    configureForPlatform()
}

internal expect fun HttpClientConfig<*>.configureForPlatform()

expect fun initStore(): KVault


class BaseUrl(
    private val debug :Boolean = true,
    val url:String = if (debug) "http://10.0.2.2" else "http://172.20.10.2",
    val port :String = "8000"
){
    fun getMainUrl():String{
        return "$url:$port"
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
}

fun Module.viewModel(){
    viewModelDefinition {
        AuthenticationViewModel( get(),get(),get() )
    }
    viewModelDefinition {
        RibbonViewModel( get() )
    }
    viewModelDefinition {
        SplashPageViewModel(get(),get(),get())
    }
    viewModelDefinition {
        NewViewModel(get(),get())
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