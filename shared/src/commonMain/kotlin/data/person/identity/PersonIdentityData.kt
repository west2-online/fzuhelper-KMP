package data.person.identity

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class PersonIdentityData(
    val code: Int,
    val `data`: List<Data>?,
    val msg: String
){
    fun toNetworkResult(): NetworkResult<PersonIdentityData> {
        return when(code){
            0 -> NetworkResult.Success<PersonIdentityData>(this)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}