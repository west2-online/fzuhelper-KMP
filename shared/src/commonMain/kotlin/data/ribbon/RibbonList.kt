package data.ribbon

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class RibbonList(
    val code: Int,
    val `data`: List<RibbonData>,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<List<RibbonData>> {
        return when(code){
            1 -> NetworkResult.Success(this.data)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}


