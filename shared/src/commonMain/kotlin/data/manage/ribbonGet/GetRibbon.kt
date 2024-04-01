package data.manage.ribbonGet

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class GetRibbon(
    val code: Int,
    val `data`: List<RibbonData>,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<List<RibbonData>> {
        return when(code){
            0 -> networkErrorWithLog(code,"获取失败")
            1 -> NetworkResult.Success(this.data)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}

