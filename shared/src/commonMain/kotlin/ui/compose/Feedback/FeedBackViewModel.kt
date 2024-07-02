package ui.compose.Feedback

import androidx.paging.PagingSource
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import data.feedback.github.githubComment.GithubCommentsItem
import data.feedback.github.githubIssue.GithubIssue
import data.feedback.github.githubIssueListByPage.GithubIssueByPageItem
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import repository.FeedbackRepository
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 反馈逻辑
 * @property feedbackRepository FeedbackRepository
 * @property _submitResult CMutableStateFlow<NetworkResult<String>>
 * @property submitResult StateFlow<NetworkResult<String>> 发布的结果
 * @property _detailResult CMutableStateFlow<NetworkResult<Data>>
 * @property detailResult StateFlow<NetworkResult<Data>> 获取详情的结果
 * @property _commentResult CMutableStateFlow<NetworkResult<String>>
 * @property commentResult StateFlow<NetworkResult<String>> 评论的结果
 * @property feedbackListFlow Flow<PagingData<Data>> 反馈list
 * @constructor
 */
class FeedBackViewModel(
    private val feedbackRepository: FeedbackRepository
):ViewModel() {
    private val _submitResult = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val submitResult = _submitResult.asStateFlow()

    private val _detailResult = CMutableStateFlow(MutableStateFlow<NetworkResult<GithubIssue>>(
        NetworkResult.UnSend()))
    val detailResult = _detailResult.asStateFlow()

    init {
        viewModelScope.launch {
            detailResult.collect{
                viewModelScope.launch {
                    detailComment.value = when(it){
                        is NetworkResult.Error -> {
                            NetworkResult.Error<List<GithubCommentsItem>>(it.errorForShow,it.rawError)
                        }
                        is NetworkResult.LoadingWithAction -> {
                            NetworkResult.LoadingWithAction()
                        }
                        is NetworkResult.LoadingWithOutAction -> {
                            NetworkResult.LoadingWithOutAction<List<GithubCommentsItem>>()
                        }
                        is NetworkResult.Success -> {
                            if(it.dataForShow.number != null){
                                var data:NetworkResult<List<GithubCommentsItem>> = NetworkResult.UnSend<List<GithubCommentsItem>>()
                                feedbackRepository.getFeedbackDetailCommentsByGithub(it.dataForShow.number)
                                    .flowOn(Dispatchers.IO)
                                    .catch {
                                        data =  NetworkResult.Error(Throwable("获取失败"),Throwable("获取失败"))
                                        println("_______________${it.message}")
                                    }.collect{
                                        data =  NetworkResult.Success<List<GithubCommentsItem>>(it)
                                        println(data)
                                    }
                                data
                            }else{
                                NetworkResult.Error(Throwable("获取失败"),Throwable("获取失败"))
                            }

                        }
                        is NetworkResult.UnSend -> NetworkResult.UnSend<List<GithubCommentsItem>>()
                        else -> {
                            NetworkResult.Error<List<GithubCommentsItem>>(Throwable(""),Throwable(""))
                        }
                    }
                }

            }

        }
    }
    val detailComment = MutableStateFlow<NetworkResult<List<GithubCommentsItem>>>(NetworkResult.UnSend())


    private val _commentResult = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val commentResult = _commentResult.asStateFlow()

    /**
     * 发布反馈
     * @param content String
     * @param type FeedbackType
     */
    fun submitNewFeedback(content : String,title:String,label:List<String>){
        viewModelScope.launch {
            _submitResult.logicIfNotLoading {
                if(title == ""){
                    _submitResult.resetWithLog("submitNewFeedBack/submitNewFeedBack",NetworkResult.Error(Throwable("标题不得为空"),Throwable("标题不得为空")))
                }
                if(content == ""){
                    _submitResult.resetWithLog("submitNewFeedBack/submitNewFeedBack",NetworkResult.Error(Throwable("内容不得为空"),Throwable("内容不得为空")))
                }
                feedbackRepository.submitNewFeedBack(content,title,label)
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

    /**
     * 获取特定的反馈详情
     * @param id Int
     */
    fun getFeedbackDetail(id:Long){
        viewModelScope.launch {
            _detailResult.logicIfNotLoading {
                feedbackRepository.getFeedbackDetailByGithub(id)
                    .actionWithLabel(
                        "getFeedbackDetailFromGithub/getFeedbackDetail",
                        catchAction = { label, error ->
                            _detailResult.resetWithLog(label, networkErrorWithLog(error,"获取详情失败"))
                        },
                        collectAction = { label, data ->
                            _detailResult.resetWithLog(label, NetworkResult.Success(data))
                        }
                    )

            }
        }
    }

    /**
     * 发布反馈评论
     * @param content String
     * @param id Int
     */
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

    /**
     * Post list flow
     * 反馈列表分页
     */
    val feedbackListFlow = Pager(
        PagingConfig(
            pageSize = 10,
            prefetchDistance = 2
        ),
    ){
        EasyFeedbackPageSource(
            backend = LoadFeedBackPageData {
                val data = feedbackRepository.getGithubIssues(it).first()
                return@LoadFeedBackPageData data
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
    val getFeedbackPageData : suspend (page:Int) -> List<GithubIssueByPageItem>?
) {
    suspend fun searchFeedbacks(page: Int): FeedbackPageData {
        val response = getFeedbackPageData(page)
        return FeedbackPageData(
            response,
            when{
                response == null -> null
                response.isEmpty() -> null
                response.size < 10 -> null
                else -> ( page + 1 )
            }
        )
    }
}


data class FeedbackPageData(
    val result : List<GithubIssueByPageItem>?,
    val nextPageNumber: Int?
)


class EasyFeedbackPageSource(
    private val backend: LoadFeedBackPageData,
) : PagingSource<Int,GithubIssueByPageItem>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, GithubIssueByPageItem> {
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

    override fun getRefreshKey(state: PagingState<Int, GithubIssueByPageItem>): Int? {
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
