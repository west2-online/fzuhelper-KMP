package data.emptyRoom

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class EmptyData(val code: Int, val `data`: Data, val msg: String) {
  fun toNetworkResult(): NetworkResult<Map<String, List<EmptyItemData>?>?> {
    return when (code) {
      0 -> NetworkResult.Success(data.EmptyRoomList)
      else -> networkErrorWithLog(code, "获取失败，请稍后再试")
    }
  }
}
