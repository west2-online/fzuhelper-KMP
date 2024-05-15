package data.person.UserData

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class UserData(
    val code: Int,
    val `data`: Data?,
    val msg: String
){
    fun toNetworkResult():NetworkResult<UserData>{
        return when(code){
            0 -> NetworkResult.Success<UserData>(this)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}