package data.emptyRoom

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class EmptyRoomData(
    val code: Int,
    val `data`: Map<String,List<EmptyRoom>?>?,
    val msg: String?
) {
    fun toNetworkResult(): NetworkResult<Map<String, List<EmptyRoom>?>?> {
        return when(code){
            1 -> NetworkResult.Success(data)
            2 -> networkErrorWithLog(code,UnAvailable(code))
            3,4 -> NetworkResult.Success(data)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}

class UnAvailable(
    val key : Int
):Throwable()

