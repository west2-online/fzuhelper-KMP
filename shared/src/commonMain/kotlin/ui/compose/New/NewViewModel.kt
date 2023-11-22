package ui.compose.New

import com.liftric.kvault.KVault
import data.post.PostById.PostById
import data.post.PostList.PostList
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import repository.PostRepository
import ui.route.Route
import ui.route.RouteState
import ui.util.network.NetworkResult
import ui.util.network.reset

class NewViewModel(
    private val postRepository:PostRepository,
    private val routeState:RouteState,
    private val kVault: KVault
):ViewModel() {
    private val _currentPostDetail = CMutableStateFlow(MutableStateFlow<NetworkResult<PostById>>(NetworkResult.UnSend()))
    val currentPostDetail = _currentPostDetail.asStateFlow()

    private val _postList = CMutableStateFlow(MutableStateFlow<NetworkResult<PostList>>(NetworkResult.UnSend()))
    val postList = _postList.asStateFlow()

    fun getPostById(id: String){
        viewModelScope.launch (Dispatchers.IO){
            _currentPostDetail.reset(NetworkResult.Loading())
            postRepository.getPostById(id = id)
                .catch {
                    println(it.message)
                    _currentPostDetail.reset(NetworkResult.Error(Throwable("帖子获取失败")))
                }
                .collect{
                    _currentPostDetail.reset(NetworkResult.Success(it))
                    println(_currentPostDetail.value)
                }
        }
    }
    fun getPostByPage(page: String){
        viewModelScope.launch (Dispatchers.IO){
            _currentPostDetail.reset(NetworkResult.Loading())
            postRepository.getPostByPage(page = page)
                .catch {
                    _postList.reset(NetworkResult.Error(Throwable("帖子获取失败")))
                }
                .collect{
                    _postList.reset(NetworkResult.Success(it))
                }
        }
    }
    fun navigateToRelease(token:String){
        routeState.navigateWithoutPop(Route.ReleasePage(token))
    }
}
