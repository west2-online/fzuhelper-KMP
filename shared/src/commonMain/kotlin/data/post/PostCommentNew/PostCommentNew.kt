package data.post.PostCommentNew

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class PostCommentNew(val code: Int, val `data`: String?, val msg: String?) {
  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      1 -> networkErrorWithLog(code, "评论不得为空")
      0 -> NetworkResult.Success("评论成功")
      else -> networkErrorWithLog(code, "评论失败,稍后再试")
    }
  }
}
