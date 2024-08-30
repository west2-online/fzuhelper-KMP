package data.manage.ribbonDelete

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class RibbonDelete(val code: Int, val `data`: String?, val msg: String) {

  fun toNetworkResult(): NetworkResult<String> {
    return when (code) {
      0 -> NetworkResult.Success("删除成功")
      else -> networkErrorWithLog(code, "删除失败")
    }
  }
}
