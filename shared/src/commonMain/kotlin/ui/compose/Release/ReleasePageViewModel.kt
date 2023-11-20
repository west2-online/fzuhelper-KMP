package ui.compose.Release

import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import repository.ReleaseRepository
import ui.route.RouteState

class ReleasePageViewModel(private val releaseRepository: ReleaseRepository, val routeState: RouteState):ViewModel() {
    fun newPost(releasePageItemList:List<ReleasePageItem>,title:String){
        viewModelScope.launch(Dispatchers.IO){
            releaseRepository.newPost(
                releasePageItemList.filter {
                    when(it){
                        is ReleasePageItem.TextItem -> {
                            return@filter it.text.value != ""
                        }
                        is ReleasePageItem.ImageItem -> {
                            return@filter it.image.value != null
                        }
                        else -> {
                            return@filter false
                        }
                    }
                },
                title = title
            ).catch {
                println(it.message)
            }.collect {
                println(it)
            }
        }
    }
}