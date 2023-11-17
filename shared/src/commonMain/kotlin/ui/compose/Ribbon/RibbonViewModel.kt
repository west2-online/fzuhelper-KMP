package ui.compose.Ribbon

import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import ui.route.Route
import ui.route.RouteState

class RibbonViewModel(
    private val routeState: RouteState,
    private val kVault: KVault,
):ViewModel() {
    fun enterFunction(route: Route){
        routeState.navigateWithoutPop(route)
    }
}