package data.splash

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class Splash(
    val code: Int,
    val `data`: String?,
    val msg: String?
) {
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            1 -> networkErrorWithLog(code,"加载失败")
            0 -> NetworkResult.Success(data.toString())
            else -> networkErrorWithLog(code,"加载失败")
        }
    }
}