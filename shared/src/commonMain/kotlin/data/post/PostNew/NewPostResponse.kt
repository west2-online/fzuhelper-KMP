package data.post.PostNew

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class NewPostResponse(val code: Int, val `data`: String?, val msg: String?) {
  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      0 -> networkErrorWithLog(code, "缺少标题")
      1 -> NetworkResult.Success("发布成功")
      else -> networkErrorWithLog(code, "发布失败")
    }
  }
}
