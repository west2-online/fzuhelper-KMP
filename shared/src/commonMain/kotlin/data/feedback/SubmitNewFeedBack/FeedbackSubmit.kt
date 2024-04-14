package data.feedback.SubmitNewFeedBack

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class FeedbackSubmit(
    val code: Int,
    val `data`: String?,
    val msg: String?
){
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            0 -> NetworkResult.Success("发布成功")
            else -> networkErrorWithLog(code,"发布失败")
        }
    }
}

enum class FeedbackStatus(val value: Int, val description: String) {
    MissingID(0, "缺少ID"),
    FailedToGetFeedbackDetails(1, "获取反馈详情失败"),
    SuccessToGetFeedbackDetails(2, "成功获取反馈详情")
}