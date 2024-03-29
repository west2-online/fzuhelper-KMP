package data.register

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog
import util.network.networkSuccess

@Serializable
data class RegisterResponse(
    val code: Int,
    val `data`: String?,
    val msg: String?
) {
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            11 -> networkSuccess("获取验证码成功")
            0 -> networkSuccess("注册成功")
            1 -> networkErrorWithLog(code,"申请验证码时缺失邮箱")
            2 -> networkErrorWithLog(code,"申请验证码时，邮箱格式错误")
            7 -> networkErrorWithLog(code,"该电子邮件地址已经注册")
            8 -> networkErrorWithLog(code,"验证码不正确")
            10 -> networkErrorWithLog(code,"验证码申请过于频繁")
            else ->  networkErrorWithLog(code,"注册失败")
        }
    }
}

@Serializable
data class LoginResponse(
    val code: Int,
    val `data`: String?,
    val msg: String?
) {
    fun toNetworkResult(): NetworkResult<String> {
        return  when(code){
            12 -> networkSuccess("登录成功")
            16 -> networkErrorWithLog(code,"帐户密码不正确")
            17 -> networkErrorWithLog(code,"验证码错误")
            20 -> networkSuccess("获取验证码成功")
            22 -> networkErrorWithLog(code,"验证码申请过于频繁")
            23 -> networkErrorWithLog(code,"申请验证码时缺失邮箱")
            24 -> networkErrorWithLog(code,"申请验证码时，邮箱格式错误")
            else -> networkErrorWithLog(code,"登录失败")
        }
    }
}
