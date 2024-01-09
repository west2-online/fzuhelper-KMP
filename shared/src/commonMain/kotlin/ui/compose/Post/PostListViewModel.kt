package ui.compose.Post

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import com.liftric.kvault.KVault
import data.post.PostCommentNew.PostCommentNew
import data.post.PostList.Data
import data.post.PostList.PostList
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import repository.CommentSubmitStatus
import repository.PostRepository
import ui.compose.Report.ReportType
import ui.root.RootAction
import ui.root.RootTarget
import ui.util.network.NetworkResult

class PostListViewModel(
    private val postRepository:PostRepository,
    private val kVault: KVault,
    private val client: HttpClient,
    private val rootAction: RootAction
):ViewModel() {
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

    fun navigateToRelease(){
        rootAction.navigateToNewTarget(RootTarget.Release)
    }

    fun navigateToReport(type: ReportType){
        rootAction.navigateToNewTarget(RootTarget.Report(type))
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
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}





class LoadPageDataForCommentPreview(
    val getResult : suspend (page:Int) -> List<data.post.PostCommentPreview.Data>?
) {
    suspend fun searchCommentPreview(page: Int): PageLoadDataForCommentPreview {
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
    val result : List<data.post.PostCommentPreview.Data>?,
    val nextPageNumber: Int?
)


class EasyPageSourceForCommentPreview(
    private val backend: LoadPageDataForCommentPreview,
) : PagingSource<Int,data.post.PostCommentPreview.Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, data.post.PostCommentPreview.Data> {
        return try {
            val page = params.key ?: 1
            val response = backend.searchCommentPreview(page)
            LoadResult.Page(
                data = response.result!!,
                prevKey = null,
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(Throwable("加载失败"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int,  data.post.PostCommentPreview.Data>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}



class LoadPageDataForCommentTree(
    val getResult : suspend (page:Int) -> List<data.post.PostCommentTree.Data>?
) {
    suspend fun searchCommentTree(page: Int): PageLoadDataForCommentTree {
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
            val response = backend.searchCommentTree(page)
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