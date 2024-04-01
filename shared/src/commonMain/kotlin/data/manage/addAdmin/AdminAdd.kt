package data.manage.addAdmin

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class AdminAdd(
    val code: Int,
    val `data`: String?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            0 ,1 -> networkErrorWithLog(code,"添加失败")
            2 -> NetworkResult.Success("添加成功")
            else -> networkErrorWithLog(code,"添加失败")
        }
    }
}