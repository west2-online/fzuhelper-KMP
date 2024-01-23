package ui.compose.Manage

import androidx.paging.Pager
import androidx.paging.PagingSource
import app.cash.paging.PagingConfig
import app.cash.paging.cachedIn
import data.Manage.PostReportPage.PostReportContextData
import data.Manage.PostReportPage.PostReportForResponseList
import data.post.PostById.PostById
import data.post.PostById.PostData
import data.share.Comment
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import repository.ManageRepository
import repository.toNetworkResult
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.flow.launchInDefault
import ui.util.network.NetworkResult
import ui.util.network.loginIfNotLoading
import ui.util.network.reset

class ManageViewModel(
    val client : HttpClient,
    val repository: ManageRepository
):ViewModel() {

    var postReportPageList = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 2
        ),
    ){
        EasyPageSourceForPostReport(
            backend = LoadPostReportPageData {
                val postReportDataList = mutableListOf<PostReportData>()
                val postReportData = client.get("/manage/post/list/${it}").body<PostReportForResponseList>()
                postReportData.data.forEach { postReportContextData ->
                    val postById  = client.get("/post/id/${postReportContextData.Post.Id}").body<PostById>()
                    postReportDataList.add(PostReportData(
                        postReportContextData = postReportContextData,
                        postData = postById.data
                    ))
                }
                return@LoadPostReportPageData postReportDataList.toList()
            }
        )
    }.flow
    .cachedIn(viewModelScope)

    var commentReportPageList = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 2
        ),
    ){
        EasyPageSourceForPostReport(
            backend = LoadPostReportPageData {
                val postReportDataList = mutableListOf<PostReportData>()
                val postReportData = client.get("/manage/post/list/${it}").body<PostReportForResponseList>()
                postReportData.data.forEach { postReportContextData ->
                    val postById  = client.get("/post/id/${postReportContextData.Post.Id}").body<PostById>()
                    postReportDataList.add(PostReportData(
                        postReportContextData = postReportContextData,
                        postData = postById.data
                    ))
                }
                return@LoadPostReportPageData postReportDataList.toList()
            }
        )
    }.flow
        .cachedIn(viewModelScope)


    fun dealPost(reportState:MutableStateFlow<NetworkResult<String>>, postId:Int, result:PostProcessResult){
        viewModelScope.launchInDefault {
            reportState.loginIfNotLoading {
                repository.processPost(postId,result.value)
                    .catchWithMassage {
                        reportState.reset(NetworkResult.Error(Throwable("操作失败")))
                    }.collectWithMassage {
                        reportState.reset(it.toNetworkResult())
                    }
            }
        }

    }

}

enum class PostProcessResult(val value : Int){
    BanPost(2),
    PassPost(0)
}

class PostReportData(
    val postReportContextData: PostReportContextData,
    val postData: PostData,
    val state: MutableStateFlow<NetworkResult<String>> = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())
)

class CommentReportData(
    val postReportContextData: PostReportContextData,
    val postData: Comment,
    val state: MutableStateFlow<NetworkResult<String>> = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())
)

data class PageLoadDataForPostReport(
    val result : List<PostReportData>?,
    val nextPageNumber: Int?
)

class EasyPageSourceForPostReport(
    private val backend: LoadPostReportPageData,
) : PagingSource<Int, PostReportData>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, PostReportData> {
        return try {
            val page = params.key ?: 1
            val response = backend.searchPostReport(page)
            LoadResult.Page(
                data = response.result!!,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(Throwable("加载失败"))
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<Int, PostReportData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}

class LoadPostReportPageData(
    val getResult : suspend (page:Int) -> List<PostReportData>?
) {
    suspend fun searchPostReport(page: Int): PageLoadDataForPostReport {
        val response = getResult(page)
        return PageLoadDataForPostReport(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}

data class PageLoadDataForCommentReport(
    val result : List<CommentReportData>?,
    val nextPageNumber: Int?
)

class EasyPageSourceForCommentReport(
    private val backend: LoadCommentReportPageData,
) : PagingSource<Int, CommentReportData>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, CommentReportData> {
        return try {
            val page = params.key ?: 1
            val response = backend.searchPostReport(page)
            LoadResult.Page(
                data = response.result!!,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(Throwable("加载失败"))
        }
    }

    override fun getRefreshKey(state: androidx.paging.PagingState<Int, CommentReportData>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

}

class LoadCommentReportPageData(
    val getResult : suspend (page:Int) -> List<CommentReportData>?
) {
    suspend fun searchPostReport(page: Int): PageLoadDataForCommentReport {
        val response = getResult(page)
        return PageLoadDataForCommentReport(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}
