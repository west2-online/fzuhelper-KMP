package ui.compose.New

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import repository.NewRepository
import ui.route.Route
import ui.route.RouteState

class NewViewModel(val newRepository:NewRepository, private val routeState:RouteState):ViewModel() {
    fun navigateToRelease(token:String){
        routeState.navigateWithoutPop(Route.ReleasePage(token))
    }
}