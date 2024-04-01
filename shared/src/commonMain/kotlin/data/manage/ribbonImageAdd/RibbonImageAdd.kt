package data.manage.ribbonImageAdd

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class RibbonImageAdd(
    val code: Int,
    val `data`: String?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            4 -> NetworkResult.Success("添加成功")
            else -> networkErrorWithLog(code,"添加失败")
        }
    }
}
