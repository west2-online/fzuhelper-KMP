package data.post.PostById

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class PostById(val code: Int, val `data`: PostData, val msg: String) {
  fun toNetworkResult(): NetworkResult<PostData> {
    return when (code) {
      0 -> NetworkResult.Success(data)
      else -> networkErrorWithLog(code, Throwable("获取失败"))
    }
  }
}
