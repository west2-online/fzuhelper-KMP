package data.manage.userDataByEmail

import data.share.User
import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkError

@Serializable
data class UserDataByEmail(
    val code: Int,
    val `data`: User?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<User> {
        val result = UserDataByEmailResult.values().find {
            this.code == it.value
        }
        result ?:let {
            return NetworkResult.Error(Throwable("操作失败"))
        }
        result.let {
            return when(it){
                UserDataByEmailResult.FailedToObtainUserInformation -> networkError("获取失败")
                UserDataByEmailResult.FailedToQueryEmailInformation -> networkError("获取失败")
                UserDataByEmailResult.TheMailboxIsSuccessfullyAcquired -> if(this.data == null) networkError("无该用户") else NetworkResult.Success(this.data)
            }
        }
    }
}
enum class UserDataByEmailResult(val value:Int,val describe:String){
    FailedToObtainUserInformation(0,"FailedToObtainUserInformation"),
    FailedToQueryEmailInformation(1,"FailedToQueryEmailInformation"),
    TheMailboxIsSuccessfullyAcquired(2,"TheMailboxIsSuccessfullyAcquired")
}