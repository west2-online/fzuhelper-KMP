package ui.compose.Authentication

import androidx.compose.ui.graphics.ImageBitmap
import asImageBitmap
import data.LoginRepository
import data.TokenData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import ui.util.network.NetworkResult


class AuthenticationViewModel(private val loginRepository:LoginRepository) : ViewModel() {
    private val _captcha = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val captcha = _captcha.asStateFlow()

    private val _registerState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val registerState = _registerState.asStateFlow()

    private val _studentCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<ImageBitmap>>(NetworkResult.UnSend()))
    val studentCaptcha = _studentCaptcha.asStateFlow()

    private val _verifyStudentIDState = CMutableStateFlow(MutableStateFlow<NetworkResult<TokenData>>(NetworkResult.UnSend()))
    val verifyStudentIDState = _verifyStudentIDState.asStateFlow()

    fun getCaptcha(email:String){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.getCaptcha(email = email)
                .catch {
                    _captcha.value = NetworkResult.Error(Throwable("申请失败,请稍后重试"))
                }
                .collect{ authenticationResponse->
                    println(authenticationResponse)
                    RegistrationStatus.values().filter {
                        it.value == authenticationResponse.code
                    }.let {
                       if(it.isNotEmpty()){
                           _captcha.value = it[0].toNetworkResult()
                               return@collect
                       }
                    }
                    _registerState.value = NetworkResult.Success("注册成功")
                }
        }
    }

    fun register(
        email:String,password:String,captcha:String
    ){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.register(email = email, password = password, captcha = captcha)
                .catch {
                    _registerState.value = NetworkResult.Error(it)
                }.collect{ authenticationResponse->
                    _registerState.value = RegistrationStatus.values().filter {
                        it.value == authenticationResponse.code
                    }[0].toNetworkResult()
                    println(authenticationResponse)
                }
        }
    }

    fun verifyStudentID( studentCode : String, studentPassword:String,captcha:String){
        viewModelScope.launch (Dispatchers.IO){
            _verifyStudentIDState.value = NetworkResult.Loading()
            loginRepository.verifyStudentIdentity(
                userName = studentCode, password = studentPassword ,captcha = captcha
            ).catch {
                println(it.toString())
                _verifyStudentIDState.value = NetworkResult.Error(it)
            }
            .collect{
                _verifyStudentIDState.value = NetworkResult.Success(it)
            }
        }
    }

    fun refreshStudentCaptcha(){
        viewModelScope.launch(Dispatchers.IO) {
            if(_studentCaptcha.value is NetworkResult.Loading){
                return@launch
            }
            _studentCaptcha.value = NetworkResult.Loading()
            loginRepository.getVerifyCode()
                .catch {
                    println(it.message.toString())
                    _studentCaptcha.value = NetworkResult.Error(it)
                }.collect{
                    _studentCaptcha.value = NetworkResult.Success(it.asImageBitmap())
                }
        }
    }

    fun cleanRegisterData() {
        _captcha.value = NetworkResult.UnSend()
        _registerState.value = NetworkResult.UnSend()
        _studentCaptcha.value = NetworkResult.UnSend()
        _verifyStudentIDState.value = NetworkResult.UnSend()
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
    CaptchaVerificationFailed(9, "验证码验证失败"),
    RequestsForVerificationCodesAreTooFrequent(10,"验证码申请过于频繁"),
    TheVerificationCodeWasObtained(11,"获取验证码成功")
}

fun RegistrationStatus.toNetworkResult():NetworkResult<String>{
    return when(this){
        RegistrationStatus.RegisterSuccess -> NetworkResult.Success("注册成功")
        RegistrationStatus.TheVerificationCodeWasObtained -> NetworkResult.Success("获取验证码成功")
        RegistrationStatus.CaptchaVerificationFailed,RegistrationStatus.TheVerificationCodeIsIncorrect -> NetworkResult.Error(Throwable("验证码验证失败"))
        RegistrationStatus.ThisEmailAddressIsAlreadyRegistered -> NetworkResult.Error(Throwable("该邮箱已经注册"))
        RegistrationStatus.TheEmailIsMissingWhenAskFoCaptcha,RegistrationStatus.TheInformationIsEmpty->NetworkResult.Error(Throwable("注册信息不足"))
        RegistrationStatus.TheEmailIsFormatErrorWhenAskFoCaptcha -> NetworkResult.Error(Throwable("邮箱格式错误"))
        RegistrationStatus.RequestsForVerificationCodesAreTooFrequent -> NetworkResult.Error(Throwable("验证码申请过于频繁"))
        else ->  NetworkResult.Error(Throwable("未知错误，请稍后重试"))
    }
}