package ui.compose.Authentication

import androidx.compose.ui.graphics.ImageBitmap
import asImageBitmap
import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import repository.LoginRepository
import repository.TokenData
import ui.route.Route
import ui.route.RouteState
import ui.util.network.NetworkResult
import ui.util.network.reset


class AuthenticationViewModel(private val loginRepository: LoginRepository, private val kVault: KVault, val routeState:RouteState) : ViewModel() {
    private val _captcha = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val captcha = _captcha.asStateFlow()

    private val _registerState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val registerState = _registerState.asStateFlow()

    private val _studentCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<ImageBitmap>>(NetworkResult.UnSend()))
    val studentCaptcha = _studentCaptcha.asStateFlow()

    private val _verifyStudentIDState = CMutableStateFlow(MutableStateFlow<NetworkResult<TokenData>>(NetworkResult.UnSend()))
    val verifyStudentIDState = _verifyStudentIDState.asStateFlow()


    private val _loginState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val loginState = _loginState.asStateFlow()

    private val _loginCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val loginCaptcha = _loginCaptcha.asStateFlow()

    fun getRegisterCaptcha(email:String){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.getRegisterCaptcha(email = email)
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
                    _captcha.value = NetworkResult.Success("发送成功")
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
                    RegistrationStatus.values().filter {
                        it.value == authenticationResponse.code
                    }.let {
                        if(it.isNotEmpty()){
                            _registerState.reset(it.first().toNetworkResult())
                        }
                    }
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
                _verifyStudentIDState.reset(NetworkResult.Success(it))
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

    fun login(email: String,password: String,captcha:String){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.login(email = email, password = password, captcha = captcha)
                .catch {
                    _loginState.value = NetworkResult.Error(it)
                }.collect{ authenticationResponse->
                    RegistrationStatus.values().filter {
                        it.value == authenticationResponse.code
                    }.let {
                        if(it.isNotEmpty()){
                            _loginState.reset(it.first().toNetworkResult())
                        }
                    }
                    if(authenticationResponse.code == RegistrationStatus.LoginSuccessful.value){
                        kVault.set("token",authenticationResponse.data.toString())
                    }
                    println(authenticationResponse)
                    enterAuthor()
                }
        }
    }

    fun cleanRegisterData() {
        _captcha.value = NetworkResult.UnSend()
        _registerState.value = NetworkResult.UnSend()
        _studentCaptcha.value = NetworkResult.UnSend()
        _verifyStudentIDState.value = NetworkResult.UnSend()
        _loginCaptcha.value = NetworkResult.UnSend()
        _loginState.value = NetworkResult.UnSend()
    }

    fun getLoginCaptcha(email: String){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.getLoginCaptcha(email = email)
                .catch {
                    _loginCaptcha.value = NetworkResult.Error(Throwable("申请失败,请稍后重试"))
                }
                .collect{ authenticationResponse->
                    println(authenticationResponse)
                    RegistrationStatus.values().filter {
                        it.value == authenticationResponse.code
                    }.let {
                        if(it.isNotEmpty()){
                            _loginCaptcha.value = it[0].toNetworkResult()
                            return@collect
                        }
                    }
                    _loginCaptcha.value = NetworkResult.Success("注册成功")
                }
        }
    }

    private fun enterAuthor(){
        val token : String? = kVault.string(forKey = "token")
        token ?: return
        routeState.navigateWithPop(Route.Splash())
    }

}

enum class RegistrationStatus(val value: Int, val description: String,val descriptionForToast:String? = null) {
    // 注册相关
    RegisterSuccess(0, "注册成功"),
    TheEmailIsMissingWhenAskFoCaptcha(1, "申请验证码时缺失邮箱"),
    TheEmailIsFormatErrorWhenAskFoCaptcha(2, "申请验证码时，邮箱格式错误"),
    TheVerificationCodeWasNotGenerated(3, "验证码生成失败","注册失败"),
    EmailFailedToSend(4, "验证码邮件发送失败","注册失败"),
    TheInformationIsEmpty(5, "注册时信息为空"),
    StorageSystemRegistrationError(6, "因为mysql或者redis的问题导致的无法注册","注册失败"),
    ThisEmailAddressIsAlreadyRegistered(7, "该电子邮件地址已经注册"),
    TheVerificationCodeIsIncorrect(8, "验证码不正确"),
    CaptchaVerificationFailed(9, "验证码验证失败","注册失败"),
    RequestsForVerificationCodesAreTooFrequent(10, "验证码申请过于频繁"),
    TheVerificationCodeWasObtained(11, "获取验证码成功"),

    // 登录相关
    LoginSuccessful(12, "登录成功"),
    IncompleteLoginInformation(13, "登录信息不完整"),
    TheVerificationFailed(14, "登录验证失败"),
    JWTGenerationFailed(15, "JWT生成失败","登录失败"),
    TheAccountPasswordIsIncorrect(16, "帐户密码不正确"),
    TheVerificationCodeIsIncorrectWhenLogin(17, "验证码错误"),
    CaptchaVerificationFailedWhenLogin(18, "验证码验证失败","登录失败"),
    TheInformationIsEmptyWhenLogin(19, "登录时信息为空"),
    TheVerificationCodeWasObtainedWhenLogin(20, "获取验证码成功"),
    TheVerificationCodeWasNotGeneratedWhenLogin(21, "验证码生成失败","登录失败"),
    RequestsForVerificationCodesAreTooFrequentWhenLogin(22, "验证码申请过于频繁"),
    TheEmailIsMissingWhenAskFoCaptchaWhenLogin(23, "申请验证码时缺失邮箱"),
    TheEmailIsFormatErrorWhenAskFoCaptchaWhenLogin(24, "申请验证码时，邮箱格式错误")
}

fun RegistrationStatus.toNetworkResult():NetworkResult<String>{
    return when(this.value){
        0,1,24,12,20 -> if(this.descriptionForToast!=null) NetworkResult.Success(this.descriptionForToast) else NetworkResult.Success(this.description)
        else -> if(this.descriptionForToast!=null) NetworkResult.Error(Throwable(this.descriptionForToast)) else NetworkResult.Error(
            Throwable(this.description))
    }
}
