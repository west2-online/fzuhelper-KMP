package ui.compose.Release

import data.post.NewPostResponse
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.PostRepository
import repository.PostStatus
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.network.NetworkResult
import ui.util.network.logicIfNotLoading
import ui.util.network.reset

class ReleasePageViewModel(private val releaseRepository: PostRepository):ViewModel() {
    private val _newPostState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val newPostState = _newPostState.asStateFlow()

    //发布新的帖子
    fun newPost(releasePageItemList:List<ReleasePageItem>,title:String){
        viewModelScope.launch(Dispatchers.IO){
            _newPostState.logicIfNotLoading{
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
                    _newPostState.reset(NetworkResult.Error(Throwable("帖子不得为空")))
                    return@logicIfNotLoading
                }
                if (title.isEmpty()){
                    _newPostState.reset(NetworkResult.Error(Throwable("标题不得为空")))
                    return@logicIfNotLoading
                }
                releaseRepository.newPost(
                    releasePageItemList = list,
                    title = title
                ).catchWithMassage {
                    _newPostState.reset(NetworkResult.Error(Throwable("发布失败")))
                }.collectWithMassage { newPostResponse ->
                    _newPostState.reset(newPostResponse.toNetworkResult())
                }
            }
        }
    }

}

fun NewPostResponse.toNetworkResult():NetworkResult<String>{
    val status = PostStatus.values().find {
        it.value == this.code
    }
    status?:let {
        return NetworkResult.Error(Throwable("发布失败"))
    }
    return status.let {
        when(status.value){
            0 -> NetworkResult.Error(Throwable(status.translation))
            4 -> NetworkResult.Success("发布成功")
            else -> NetworkResult.Error(Throwable("发布失败"))
        }
    }
}