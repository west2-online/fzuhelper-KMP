package ui.compose.Feedback

import androidx.paging.PagingSource
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import data.feedback.FeedbackDetailComment.FeedbackDetailComment
import data.feedback.FeedbackList.FeedbackList
import data.feedback.FeelbackDetail.Data
import data.feedback.FeelbackDetail.FeedbackDetail
import data.feedback.SubmitNewFeedBack.FeedbackSubmit
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.FeedbackRepository
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.network.NetworkResult
import util.network.loading
import util.network.reset

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
            _submitResult.loading()
            feedbackRepository.submitNewFeedBack(content,type.code)
                .catchWithMassage {
                    _submitResult.reset(NetworkResult.Error(it))
                }
                .collectWithMassage{
                    _submitResult.reset(it.toSubmitResult())
                }
        }
    }
    fun getFeedbackDetail(id:Int){
        viewModelScope.launch {
            _detailResult.loading()
            feedbackRepository.getFeedbackDetail(id)
                .catchWithMassage {
                    _detailResult.reset(NetworkResult.Error(it))
                }
                .collectWithMassage{
                    _detailResult.reset(it.toDetailResult())
                }
        }
    }
    fun postFeedbackDetailComment(content:String,id: Int){
        viewModelScope.launch {
            _commentResult.loading()
            feedbackRepository.postFeedbackDetailComment(content,id)
                .catchWithMassage {
                    _commentResult.reset(NetworkResult.Error(it))
                }
                .collectWithMassage{
                    _commentResult.reset(it.toNetworkResult())
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



fun FeedbackSubmit.toSubmitResult(): NetworkResult<String> {
    val response = SubmitStatus.values().findLast {
        it.value == this.code
    }
    response?:let{
        return NetworkResult.Error(Throwable("数据为空"))
    }
    response.let {
        return when(it.value){
            2-> NetworkResult.Success("发布成功")
            else -> NetworkResult.Error(Throwable("发布失败"))
        }
    }
}
enum class FeedbackStatus(val value: Int, val description: String) {
    MissingID(0, "缺少ID"),
    FailedToGetFeedbackDetails(1, "获取反馈详情失败"),
    SuccessToGetFeedbackDetails(2, "成功获取反馈详情")
}


fun FeedbackDetail.toDetailResult(): NetworkResult<Data> {
    val response = SubmitStatus.values().findLast {
        it.value == this.code
    }
    response?:let{
        return NetworkResult.Error(Throwable("数据为空"))
    }
    response.let {
        return NetworkResult.Success(this.data)
    }
}

enum class CommentStatus(val value: Int, val description: String) {
    MissingCommentID(0, "缺少评论id"),
    CommentFailed(1, "评论失败"),
    TheReviewWasSuccessful(2, "评论成功")
}


fun FeedbackDetailComment.toNetworkResult(): NetworkResult<String> {
    val response = SubmitStatus.values().findLast {
        it.value == this.code
    }
    response?:let{
        return NetworkResult.Error(Throwable("数据为空"))
    }
    response.let {
        return when(it.value){
            2-> NetworkResult.Success("评论成功")
            else -> NetworkResult.Error(Throwable("评论失败"))
        }
    }
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
