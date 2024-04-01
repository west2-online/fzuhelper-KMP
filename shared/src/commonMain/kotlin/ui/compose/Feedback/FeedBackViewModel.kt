package ui.compose.Feedback

import androidx.paging.PagingSource
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import data.feedback.FeedbackList.FeedbackList
import data.feedback.FeelbackDetail.Data
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.FeedbackRepository
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

class FeedBackViewModel(
    private val feedbackRepository: FeedbackRepository
):ViewModel() {
    private val _submitResult = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val submitResult = _submitResult.asStateFlow()

    private val _detailResult = CMutableStateFlow(MutableStateFlow<NetworkResult<Data>>(
        NetworkResult.UnSend()))
    val detailResult = _detailResult.asStateFlow()

    private val _commentResult = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val commentResult = _commentResult.asStateFlow()

    fun submitNewFeedback(content : String,type: FeedbackType){
        viewModelScope.launch {
            _submitResult.logicIfNotLoading {
                feedbackRepository.submitNewFeedBack(content,type.code)
                    .actionWithLabel(
                        "submitNewFeedBack/submitNewFeedBack",
                        collectAction = { label, data ->
                            _submitResult.resetWithLog(label,data.toNetworkResult())
                        },
                        catchAction = { label, error ->
                            _submitResult.resetWithLog(label, networkErrorWithLog(error,"发布失败"))
                        }
                    )
            }
        }
    }
    fun getFeedbackDetail(id:Int){
        viewModelScope.launch {
            _detailResult.logicIfNotLoading {
                feedbackRepository.getFeedbackDetail(id)
                    .actionWithLabel(
                        "getFeedbackDetail/getFeedbackDetail",
                        catchAction = { label, error ->
                            _detailResult.resetWithLog(label, networkErrorWithLog(error,"获取详情失败"))
                        },
                        collectAction = { label, data ->
                            _detailResult.resetWithLog(label, data.toNetworkResult())
                        }
                    )

            }
        }
    }

    fun postFeedbackDetailComment(content:String,id: Int){
        viewModelScope.launch {
            _commentResult.logicIfNotLoading{
                feedbackRepository.postFeedbackDetailComment(content,id)
                    .actionWithLabel(
                        "postFeedbackDetailComment/postFeedbackDetailComment",
                        catchAction = { label, error ->
                            _commentResult.resetWithLog(label, networkErrorWithLog(error,"发表评论失败"))
                        },
                        collectAction = { label, data ->
                            _commentResult.resetWithLog(label, data.toNetworkResult())
                        }
                    )
            }
        }
    }

    val postListFlow = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 2
        ),
    ){
        EasyFeedbackPageSource(
            backend = LoadFeedBackPageData {
                return@LoadFeedBackPageData feedbackRepository.client.get("/feedback/page/${it}").body<FeedbackList>().data
            }
        )
    }.flow
        .cachedIn(viewModelScope)
}

enum class SubmitStatus(val value: Int, val description: String) {
    CreationFailed(0, "创建失败"),
    MissingInformation(1, "缺少信息"),
    CreationIsSuccessful(2, "创建成功")
}







enum class CommentStatus(val value: Int, val description: String) {
    MissingCommentID(0, "缺少评论id"),
    CommentFailed(1, "评论失败"),
    TheReviewWasSuccessful(2, "评论成功")
}





class LoadFeedBackPageData(
    val getResult : suspend (page:Int) -> List<data.feedback.FeedbackList.Data>?
) {
    suspend fun searchFeedbacks(page: Int): FeedbackPageData {
        val response = getResult(page)
        return FeedbackPageData(
            response,
            when{
                response!!.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}


data class FeedbackPageData(
    val result : List<data.feedback.FeedbackList.Data>?,
    val nextPageNumber: Int?
)


class EasyFeedbackPageSource(
    private val backend: LoadFeedBackPageData,
) : PagingSource<Int, data.feedback.FeedbackList.Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, data.feedback.FeedbackList.Data> {
        return try {
            val page = params.key ?: 1
            val response = backend.searchFeedbacks(page)
            LoadResult.Page(
                data = response.result!!,
                prevKey = null, // Only paging forward.
                nextKey = response.nextPageNumber
            )
        } catch (e: Exception) {
            LoadResult.Error(Throwable("加载失败"))
        }
    }

    override fun getRefreshKey(state: PagingState<Int, data.feedback.FeedbackList.Data>): Int? {
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
