package data.modifer

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class ModifierData(
    val code: Int,
    val `data`: String?,
    val msg: String?
){
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            2 -> NetworkResult.Success("更新失败")
            0 -> networkErrorWithLog(code,"年龄必须为数字")
            else -> networkErrorWithLog(code,"更新失败")
        }
    }
}

@Serializable
data class ModifierAvatar(
    val code: Int,
    val `data`: String?,
    val msg: String?
){
    fun toNetworkResult(): NetworkResult<String>{
        return when(code){
            3 -> NetworkResult.Success("头像更新成功")
            else -> networkErrorWithLog(code,"修改失败")
        }
    }
}