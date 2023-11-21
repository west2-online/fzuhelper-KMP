package ui.compose.Release

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import repository.PostRepository
import repository.PostStatus
import ui.route.RouteState
import ui.util.network.NetworkResult
import ui.util.network.reset

class ReleasePageViewModel(private val releaseRepository: PostRepository, val routeState: RouteState):ViewModel() {
    private val _newPostState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val newPostState = _newPostState.asStateFlow()

    //发布新的帖子
    fun newPost(releasePageItemList:List<ReleasePageItem>,title:String){
        viewModelScope.launch(Dispatchers.IO){
            if(newPostState.value is NetworkResult.Loading){
                return@launch
            }
            val list = releasePageItemList.filter {
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
            }
            if (list.isEmpty()){
                _newPostState.reset(NetworkResult.Error(Throwable("数据错误")))
                return@launch
            }
            releaseRepository.newPost(
                list,
                title = title
            ).catch {
                _newPostState.reset(NetworkResult.Error(Throwable("发布失败")))
            }.collect { newPostResponse ->
                PostStatus.values().find {
                    it.value == newPostResponse.code
                }?.let {
                    if( it.value == PostStatus.ThePostWasPublishedSuccessfullyInPost.value){
                        _newPostState.reset(NetworkResult.Success("发布成功"))
                        return@let
                    }
                    _newPostState.reset(NetworkResult.Error(Throwable("发布失败")))
                }
            }
        }
    }
}