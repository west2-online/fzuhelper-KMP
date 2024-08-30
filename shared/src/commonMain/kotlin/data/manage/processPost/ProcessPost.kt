package data.manage.processPost

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class ProcessPost(val code: Int, val `data`: String?, val msg: String) {
  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      0 -> NetworkResult.Success("处理成功")
      else -> networkErrorWithLog(code, "操作失败")
    }
  }
}
