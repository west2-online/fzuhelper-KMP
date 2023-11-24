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
    init {
        println("newViewModel${this}")
    }
    private val _currentPostDetail = CMutableStateFlow(MutableStateFlow<NetworkResult<PostById>>(NetworkResult.UnSend()))
    val currentPostDetail = _currentPostDetail.asStateFlow()

    val postListFlow = Pager(
            PagingConfig(
                pageSize = 10,
                prefetchDistance = 2
            ),
    ){
            EasyPageSource(
                backend = LoadPageData {
                    return@LoadPageData client.get("/post/page/${it}").body<PostList>().data
                }
            )
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
    fun navigateToRelease(token:String){
        routeState.navigateWithoutPop(Route.ReleasePage(token))
    }
}

class LoadPageData(
    val getResult : suspend (page:Int) -> List<Data>?
) {
    suspend fun searchUsers(page: Int): PageLoadData {
        val response = getResult(page)
        return PageLoadData(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}


data class PageLoadData(
    val result : List<Data>?,
    val nextPageNumber: Int?
)


class EasyPageSource(
    private val backend: LoadPageData,
) : PagingSource<Int, Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Data> {
        return try {
            val page = params.key ?: 1
            delay(3000)
            val response = backend.searchUsers(page)
            LoadResult.Page(
                data = response.result!!,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
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
