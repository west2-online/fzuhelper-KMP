package data.feedback.FeedbackDetailComment

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class FeedbackDetailComment(val code: Int, val `data`: String?, val msg: String) {
  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      0 -> NetworkResult.Success("评论成功")
      else -> networkErrorWithLog(code, "评论失败")
    }
  }
}
