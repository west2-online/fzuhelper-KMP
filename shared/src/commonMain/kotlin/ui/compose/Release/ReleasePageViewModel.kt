package ui.compose.Release

import data.share.Label
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

/**
 * 发布页逻辑
 * @property releaseRepository PostRepository
 * @property _newPostState CMutableStateFlow<NetworkResult<String>>
 * @property newPostState StateFlow<NetworkResult<String>>
 * @property _labelList CMutableStateFlow<NetworkResult<List<Label>>>
 * @property labelList StateFlow<NetworkResult<List<Label>>>
 * @constructor
 */
class ReleasePageViewModel(private val releaseRepository: PostRepository):ViewModel() {
    private val _newPostState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val newPostState = _newPostState.asStateFlow()


    private var _labelList = CMutableStateFlow<NetworkResult<List<Label>>>(MutableStateFlow(NetworkResult.UnSend()))
    val labelList = _labelList.asStateFlow()

    /**
     * 发布新帖子
     * @param releasePageItemList List<ReleasePageItem>
     * @param title String
     * @param labelList List<Int>
     */
    fun newPost(
        releasePageItemList: List<ReleasePageItem>,
        title: String,
        labelList : List<Int>
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
                if (
                    list.filterIsInstance<ReleasePageItem.ImageItem>().any {
                        it.let {
                            (it.image.value?.size ?: 0) / 1024 / 8 > 5
                        }
                    }
                ){
                    _newPostState.resetWithoutLog(networkErrorWithLog(Throwable("图片文件过大"),"图片文件过大"))
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

    /**
     * Get user label
     * 获取用户可用的label
     */
    fun getUserLabel(){
        viewModelScope.launch {
            _labelList.logicIfNotLoading {
                releaseRepository.getUserLabel()
                    .actionWithLabel(
                        "getUserLabel/getUserLabel",
                        catchAction = {label, error ->
                            _labelList.resetWithLog(label, networkErrorWithLog(error,"获取标签失败"))
                        },
                        collectAction = { label, data ->
                            _labelList.resetWithoutLog(NetworkResult.Success(data.data))
                        }
                    )
            }
        }
    }

}
