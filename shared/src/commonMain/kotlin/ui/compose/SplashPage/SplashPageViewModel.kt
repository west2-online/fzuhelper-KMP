package ui.compose.SplashPage

import com.liftric.kvault.KVault
import data.SplashRepository
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import ui.route.Route
import ui.route.RouteState

class SplashPageViewModel(private val splashRepository: SplashRepository, private val kVault: KVault, val routeState: RouteState):ViewModel() {
    fun navigateToMain(){
//        route.removeLast()

        routeState.navigateWithPop(Route.Main("1"))
    }
}