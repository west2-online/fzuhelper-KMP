package ui.compose.New

import androidx.compose.runtime.mutableStateOf
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import com.liftric.kvault.KVault
import data.post.PostById.PostById
import data.post.PostComment.PostCommentListPreview
import data.post.PostList.Data
import data.post.PostList.PostList
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
    private val kVault: KVault,
    private val client: HttpClient
):ViewModel() {
    init {
        println("newViewModel${this}")
    }
    val currentItem = mutableStateOf<NewItem>(NewItem.NewList())


    private val _currentPostDetail = CMutableStateFlow(MutableStateFlow<NetworkResult<PostById>>(NetworkResult.UnSend()))
    val currentPostDetail = _currentPostDetail.asStateFlow()

    var postListFlow = Pager(
            PagingConfig(
                pageSize = 10,
                prefetchDistance = 2
            ),
    ){
            EasyPageSourceForPost(
                backend = LoadPageDataForPost {
                    return@LoadPageDataForPost client.get("/post/page/${it}").body<PostList>().data
                }
            )
        }.flow
        .cachedIn(viewModelScope)

    private val _postCommentPreviewFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, data.post.PostComment.Data>?>(null))
    var postCommentPreviewFlow = _postCommentPreviewFlow.asStateFlow()


    fun getPostCommentPreview(postId: String){
        viewModelScope.launch {
            _postCommentPreviewFlow.value = Pager(
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2
                ),
            ){
                EasyPageSourceForCommentPreview(
                    backend = LoadPageDataForCommentPreview {
                        return@LoadPageDataForCommentPreview client.get("post/comment/page/${it}/${postId}").body<PostCommentListPreview>().data
                    }
                )
            }
        }
    }
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

class LoadPageDataForPost(
    val getResult : suspend (page:Int) -> List<Data>?
) {
    suspend fun searchUsers(page: Int): PageLoadDataForPost {
        val response = getResult(page)
        return PageLoadDataForPost(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}


data class PageLoadDataForPost(
    val result : List<Data>?,
    val nextPageNumber: Int?
)


class EasyPageSourceForPost(
    private val backend: LoadPageDataForPost,
) : PagingSource<Int, Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, Data> {
        return try {
            val page = params.key ?: 1
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



class LoadPageDataForCommentPreview(
    val getResult : suspend (page:Int) -> List<data.post.PostComment.Data>?
) {
    suspend fun searchUsers(page: Int): PageLoadDataForCommentPreview {
        val response = getResult(page)
        return PageLoadDataForCommentPreview(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}


data class PageLoadDataForCommentPreview(
    val result : List<data.post.PostComment.Data>?,
    val nextPageNumber: Int?
)


class EasyPageSourceForCommentPreview(
    private val backend: LoadPageDataForCommentPreview,
) : PagingSource<Int,data.post.PostComment.Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, data.post.PostComment.Data> {
        return try {
            val page = params.key ?: 1
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

    override fun getRefreshKey(state: PagingState<Int,  data.post.PostComment.Data>): Int? {
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


