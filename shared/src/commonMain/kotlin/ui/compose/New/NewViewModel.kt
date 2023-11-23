package ui.compose.New

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import com.liftric.kvault.KVault
import data.post.PostById.PostById
import data.post.PostList.Data
import data.post.PostList.PostList
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
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
    private val kVault: KVault,
    private val client: HttpClient
):ViewModel() {
    private val _currentPostDetail = CMutableStateFlow(MutableStateFlow<NetworkResult<PostById>>(NetworkResult.UnSend()))
    val currentPostDetail = _currentPostDetail.asStateFlow()

//    private val _postList = CMutableStateFlow(MutableStateFlow<NetworkResult<PostList>>(NetworkResult.UnSend()))
//    val postList = _postList.asStateFlow()
    val postListFlow = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        PagingConfig(pageSize = 10)
    ) {
        ExamplePagingSource(BackendService {
            delay(2000)
            return@BackendService client.get("/post/page/${it}").body<PostList>()
        })
    }.flow
        .cachedIn(viewModelScope)

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
//    fun getPostByPage(page: String){
//        viewModelScope.launch (Dispatchers.IO){
//            _currentPostDetail.reset(NetworkResult.Loading())
//            postRepository.getPostByPage(page = page)
//                .catch {
//                    _postList.reset(NetworkResult.Error(Throwable("帖子获取失败")))
//                }
//                .collect{
//                    _postList.reset(NetworkResult.Success(it))
//                }
//        }
//    }
    fun navigateToRelease(token:String){
        routeState.navigateWithoutPop(Route.ReleasePage(token))
    }
}


class ExamplePagingSource(
    private val backend: BackendService,
) : PagingSource<Int, Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Data> {
        return try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            val response = backend.searchUsers(nextPageNumber)
            response.result.data ?: throw Throwable("数据为空")
            LoadResult.Page(
                data = response.result.data,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error for
            // expected errors (such as a network failure).
            LoadResult.Error(Throwable("加载失败"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
        // Try to find the page key of the closest page to anchorPosition from
        // either the prevKey or the nextKey; you need to handle nullability
        // here.
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey are null -> anchorPage is the
        //    initial page, so return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

class BackendService(
    val getResult : suspend (page:Int?)->PostList
) {
    var nextPageNumber:Int? = null
    suspend fun searchUsers(nextPageNumber: Int): PageResultData {
        this.nextPageNumber  = nextPageNumber
        val response = getResult(this.nextPageNumber)
        return PageResultData(
            response,
            when{
                response.data == null -> null
                response.data.size < 10 -> null
                else -> (nextPageNumber + 1)
              }
        )
    }

}

data class PageResultData(
    val result : PostList,
    val nextPageNumber: Int?
)