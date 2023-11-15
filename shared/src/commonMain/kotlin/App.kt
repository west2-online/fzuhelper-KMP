
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import com.liftric.kvault.KVault
import data.LoginRepository
import data.SplashRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.Resource
import dev.icerock.moko.resources.desc.StringDesc
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
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
import ui.compose.Authentication.AuthenticationViewModel
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
        BackHandler(true){
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
            install(Logging)
            install(HttpCookies){

            }
            install(HttpRedirect) {
                checkHttpMethod = false
            }
            configure()
        }
    }
    single {
        SplashRepository( get() )
    }
    single {
        LoginRepository( get() )
    }
    viewModelDefinition {
        AuthenticationViewModel( get(),get(),get() )
    }
    viewModelDefinition {
        SplashPageViewModel(get(),get(),get())
    }
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
}

fun HttpClientConfig<*>.configure() {
    // TODO: Add common config
    configureForPlatform()
}

internal expect fun HttpClientConfig<*>.configureForPlatform()

expect fun initStore(): KVault
