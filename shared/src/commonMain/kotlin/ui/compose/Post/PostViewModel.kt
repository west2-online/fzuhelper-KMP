package ui.compose.Post

import androidx.compose.runtime.mutableStateOf
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import com.liftric.kvault.KVault
import data.post.PostById.PostById
import data.post.PostComment.PostCommentListPreview
import data.post.PostCommentNew.PostCommentNew
import data.post.PostCommentTree.PostCommentTree
import data.post.PostList.Data
import data.post.PostList.PostList
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import doist.x.normalize.Form
import doist.x.normalize.normalize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.CommentSubmitStatus
import repository.PostRepository
import ui.route.Route
import ui.route.RouteState
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.network.NetworkResult
import ui.util.network.loginIfNotLoading
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

    private val _postCommentTreeFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, data.post.PostCommentTree.Data>?>(null))
    var postCommentTreeFlow = _postCommentTreeFlow.asStateFlow()

    private val _commentSubmitState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val commentSubmitState = _commentSubmitState.asStateFlow()
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

    fun getPostCommentTree(treeStart: String,postId:String){
        viewModelScope.launch {
            _postCommentTreeFlow.value = Pager(
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2
                ),
            ){
                EasyPageSourceForCommentTree(
                    backend = LoadPageDataForCommentTree {
                        return@LoadPageDataForCommentTree client.get("post/commentList/page/${it}/${treeStart}/${postId}").body<PostCommentTree>().data
                    }
                )
            }
        }
    }
    fun getPostById(id: String){
        viewModelScope.launch (Dispatchers.IO){
            _currentPostDetail.reset(NetworkResult.Loading())
            postRepository.getPostById(id = id)
                .catchWithMassage {
                    println(it.message)
                    _currentPostDetail.reset(NetworkResult.Error(Throwable("帖子获取失败")))
                }
                .collectWithMassage{
                    _currentPostDetail.reset(NetworkResult.Success(it))
                    println(_currentPostDetail.value)
                }
        }
    }
    fun submitComment(parentId:Int,postId:Int,tree:String,content:String,image:ByteArray?){
        viewModelScope.launch {
            _commentSubmitState.loginIfNotLoading {
                postRepository.postNewComment(parentId,postId,tree,content.normalize(Form.NFC),image)
                    .catchWithMassage {
                        _commentSubmitState.reset(NetworkResult.Error(Throwable("评论失败，稍后再试")))
                    }.collectWithMassage{
                        _commentSubmitState.reset(it.toNetworkResult())
                    }
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



class LoadPageDataForCommentTree(
    val getResult : suspend (page:Int) -> List<data.post.PostCommentTree.Data>?
) {
    suspend fun searchUsers(page: Int): PageLoadDataForCommentTree {
        val response = getResult(page)
        return PageLoadDataForCommentTree(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}


data class PageLoadDataForCommentTree(
    val result : List<data.post.PostCommentTree.Data>?,
    val nextPageNumber: Int?
)


class EasyPageSourceForCommentTree(
    private val backend: LoadPageDataForCommentTree,
) : PagingSource<Int,data.post.PostCommentTree.Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int,data.post.PostCommentTree.Data> {
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

    override fun getRefreshKey(state: PagingState<Int,data.post.PostCommentTree.Data>): Int? {
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

fun PostCommentNew.toNetworkResult():NetworkResult<String>{
    val result = CommentSubmitStatus.values().find {
        it.value == this.code
    }
    return when(result){
        null ->{
            NetworkResult.Error(Throwable("评论失败,稍后再试"))
        }
        CommentSubmitStatus.CommentFailed,CommentSubmitStatus.FileParsingFailed, CommentSubmitStatus.FailedToSaveTheCommentImage->{
            NetworkResult.Error(Throwable("评论失败,稍后再试"))
        }
        CommentSubmitStatus.TheCommentIsEmpty -> {
            NetworkResult.Error(Throwable("评论不能为空"))
        }
        CommentSubmitStatus.TheReviewWasSuccessful -> {
            NetworkResult.Success("评论成功")
        }
        else -> {
            NetworkResult.Error(Throwable("评论失败,稍后再试"))
        }
    }
}