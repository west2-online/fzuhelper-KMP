package data.manage.processPost

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class ProcessPost(
    val code: Int,
    val `data`: String?,
    val msg: String
){
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            3 -> NetworkResult.Success("处理成功")
            0 -> networkErrorWithLog(code,"操作失败")
            else -> networkErrorWithLog(code,"操作失败")
        }
    }
}