package data.manage.openImageList

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class OpenImageList(
    val code: Int,
    val `data`: List<String>,
    val msg: String
){
    fun toNetworkResult(): NetworkResult<List<String>> {
        return when(code){
            0 -> networkErrorWithLog(code,"获取失败")
            1 -> NetworkResult.Success(this.data)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}