package data.manage.openImageDelete

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class OpenImageDelete(val code: Int, val `data`: String?, val msg: String) {
  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      4 -> NetworkResult.Success("删除成功")
      else -> networkErrorWithLog(code, "删除失败")
    }
  }
}
