package data.feedback.FeelbackDetail

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class FeedbackDetail(
    val code: Int,
    val `data`: Data,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<Data> {
        return when(code){
            0 -> NetworkResult.Success(data)
            else -> networkErrorWithLog(code,"创建失败")
        }
    }
}
