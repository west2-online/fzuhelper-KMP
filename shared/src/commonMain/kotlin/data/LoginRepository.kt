package data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import repository.register.bean.AuthenticationResponse

class LoginRepository(private val client : HttpClient) {
    fun getCaptcha(email:String):Flow<AuthenticationResponse>{
        return flow{
            val response : HttpResponse = client.post("url-string").body()
        }
    }
}

enum class RegistrationStatus(val value: Int, val description: String) {
    RegisterSuccess(0, "没有问题"),
    TheEmailIsMissingWhenAskFoCaptcha(1, "申请验证码时缺失邮箱"),
    TheEmailIsFormatErrorWhenAskFoCaptcha(2, "申请验证码时，邮箱格式错误"),
    TheVerificationCodeWasNotGenerated(3, "验证码生成失败"),
    EmailFailedToSend(4, "验证码邮件发送失败"),
    TheInformationIsEmpty(5, "注册时信息为空"),
    StorageSystemRegistrationError(6, "因为mysql或者redis的问题导致的无法注册"),
    ThisEmailAddressIsAlreadyRegistered(7, "该电子邮件地址已经注册"),
    TheVerificationCodeIsIncorrect(8, "验证码不正确"),
    CaptchaVerificationFailed(9, "验证码验证失败")
}

