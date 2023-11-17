package ui.compose.New

import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import repository.NewRepository
import ui.route.Route
import ui.route.RouteState

class NewViewModel(
    val newRepository:NewRepository,
    private val routeState:RouteState,
    private val kVault: KVault
):ViewModel() {
    fun navigateToRelease(token:String){
        routeState.navigateWithoutPop(Route.ReleasePage(token))
    }
}