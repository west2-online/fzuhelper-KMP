package ui.compose.Feedback

import androidx.paging.PagingSource
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingState
import app.cash.paging.cachedIn
import data.Feedback.FeedbackList.FeedbackList
import data.Feedback.SubmitNewFeedBack.FeedbackSubmit
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import repository.FeedbackRepository
import ui.util.network.NetworkResult
import ui.util.network.reset

class FeedBackViewModel(
    private val feedbackRepository: FeedbackRepository
):ViewModel() {
    private val _submitResult = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val submitResult = _submitResult.asStateFlow()

    fun submitNewFeedback(content : String,type: FeedbackType){
        viewModelScope.launch {
            feedbackRepository.submitNewFeedBack(content,type.code)
                .catch {
                    println(it.message)
                    _submitResult.reset(NetworkResult.Error(it))
                }
                .collect{
                    _submitResult.reset(it.toNetworkResult())
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

fun FeedbackSubmit.toNetworkResult():NetworkResult<String>{
    val response = SubmitStatus.values().findLast {
        it.value == this.code
    }
    response?:let{
        return NetworkResult.Error(Throwable("数据为空"))
    }
    response.let {
        return NetworkResult.Success("发布成功")
    }
}


class LoadFeedBackPageData(
    val getResult : suspend (page:Int) -> List<data.Feedback.FeedbackList.Data>?
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
    val result : List<data.Feedback.FeedbackList.Data>?,
    val nextPageNumber: Int?
)


class EasyFeedbackPageSource(
    private val backend: LoadFeedBackPageData,
) : PagingSource<Int, data.Feedback.FeedbackList.Data>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, data.Feedback.FeedbackList.Data> {
        return try {
            val page = params.key ?: 1
            delay(3000)
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

    override fun getRefreshKey(state: PagingState<Int, data.Feedback.FeedbackList.Data>): Int? {
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
