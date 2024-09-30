package data.base

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog


@Serializable
data class BaseResponseData<T>(
  val code: Int,
  val message: String,
  val data: T
) {
  fun toNetworkResult(): NetworkResult<T> {
    return when (code) {
      10000 -> NetworkResult.Success(data)
      else -> networkErrorWithLog(code, "获取失败")
    }
  }
}
