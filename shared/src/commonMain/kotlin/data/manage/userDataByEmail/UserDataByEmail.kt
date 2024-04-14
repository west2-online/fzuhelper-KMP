package data.manage.userDataByEmail

import data.share.User
import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class UserDataByEmail(
    val code: Int,
    val `data`: User?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<User> {
        return when(code){
            0 -> if(this.data == null) networkErrorWithLog(code,"无该用户") else NetworkResult.Success(this.data)
            else -> networkErrorWithLog(code,"获取失败")
        }
    }
}
