package data.manage.openImageAdd

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class OpenImageAdd(
    val code: Int,
    val `data`: String?,
    val msg: String
){

    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            3 -> NetworkResult.Success("操作成功")
            else -> networkErrorWithLog(code,"操作失败")
        }
    }

}