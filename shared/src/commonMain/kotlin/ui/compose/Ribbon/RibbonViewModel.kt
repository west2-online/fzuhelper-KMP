package ui.compose.Ribbon

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import ui.route.Route
import ui.route.RouteState

class RibbonViewModel(private val routeState: RouteState):ViewModel() {
    fun enterFunction(route: Route){
        routeState.navigateWithoutPop(route)
    }
}