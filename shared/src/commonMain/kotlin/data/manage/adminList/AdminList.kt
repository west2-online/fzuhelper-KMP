package data.manage.adminList

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class AdminList(
    val code: Int,
    val `data`: List<Admin>,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<List<Admin>> {
        return when(code){
            100,101 -> networkErrorWithLog(code,"权限不足")
            1 -> NetworkResult.Success(data)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}