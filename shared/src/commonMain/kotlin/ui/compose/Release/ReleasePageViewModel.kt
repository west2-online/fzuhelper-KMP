package ui.compose.Release

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.PostRepository
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog
import util.network.resetWithoutLog

class ReleasePageViewModel(private val releaseRepository: PostRepository):ViewModel() {
    private val _newPostState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val newPostState = _newPostState.asStateFlow()

    //发布新的帖子
    fun newPost(
        releasePageItemList: List<ReleasePageItem>,
        title: String,
        labelList : List<String>
    ){
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
                    _newPostState.resetWithoutLog(networkErrorWithLog(Throwable("帖子不得为空"),"帖子不得为空"))
                    return@logicIfNotLoading
                }
                if (title.isEmpty()){
                    _newPostState.resetWithoutLog(networkErrorWithLog(Throwable("标题不得为空"),"标题不得为空"))
                    return@logicIfNotLoading
                }
                if (labelList.isEmpty()){
                    _newPostState.resetWithoutLog(networkErrorWithLog(Throwable("至少要一个标签"),"至少要一个标签"))
                    return@logicIfNotLoading
                }
                releaseRepository.newPost(
                    releasePageItemList = list,
                    title = title,
                    labelList = labelList
                ).actionWithLabel(
                    "newPost/newPost",
                    collectAction = { label, data ->
                        _newPostState.resetWithLog(label,data.toNetworkResult())
                    },
                    catchAction = { label, error ->
                        _newPostState.resetWithLog(label,networkErrorWithLog(error,"发布失败"))
                    }
                )
            }
        }
    }

}
