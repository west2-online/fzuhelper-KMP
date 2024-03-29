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
import kotlinx.coroutines.launch
import repository.LoginRepository
import repository.TokenData
import ui.root.RootAction
import util.flow.actionWithLabel
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog


class AuthenticationViewModel(
    private val loginRepository: LoginRepository,
    private val kVault: KVault,
    private val rootAction: RootAction
) : ViewModel() {
    private val _captcha = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val captcha = _captcha.asStateFlow()

    private val _registerState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val registerState = _registerState.asStateFlow()

    private val _studentCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<ImageBitmap>>(
        NetworkResult.UnSend()))
    val studentCaptcha = _studentCaptcha.asStateFlow()

    private val _verifyStudentIDState = CMutableStateFlow(MutableStateFlow<NetworkResult<TokenData>>(
        NetworkResult.UnSend()))
    val verifyStudentIDState = _verifyStudentIDState.asStateFlow()


    private val _loginState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val loginState = _loginState.asStateFlow()

    private val _loginCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val loginCaptcha = _loginCaptcha.asStateFlow()

    fun getRegisterCaptcha(email:String){
        viewModelScope.launch{
            _captcha.logicIfNotLoading {
                loginRepository.getRegisterCaptcha(email = email)
                    .actionWithLabel(
                        "getRegisterCaptcha/getRegisterCaptcha",
                        catchAction = {label, error ->
                            _captcha.resetWithLog(label,NetworkResult.Error(error,Throwable("申请失败,请稍后重试")))
                        },
                        collectAction = { label, data ->
                            _captcha.resetWithLog(label,data.toNetworkResult())
                        }
                    )
            }

        }
    }

    fun register(
        email:String,password:String,captcha:String,studentCode:String,studentPassword:String
    ){
        viewModelScope.launch (Dispatchers.Default){
            _registerState.logicIfNotLoading {
                loginRepository.register(email = email, password = password, captcha = captcha,studentCode = studentCode,studentPassword = studentPassword)
                    .actionWithLabel(
                        "register/register",
                             collectAction = { label, data ->
                                 _registerState.resetWithLog(label,data.toNetworkResult())
                             },
                            catchAction = { label ,error ->
                                _registerState.resetWithLog(label, networkErrorWithLog(error,"注册失败"))
                            }
                        )
            }


        }
    }

    fun verifyStudentID( studentCode : String, studentPassword:String,captcha:String){
        viewModelScope.launchInDefault{
            _verifyStudentIDState.logicIfNotLoading {
                loginRepository.verifyStudentIdentity(
                    userName = studentCode, password = studentPassword ,captcha = captcha
                ).actionWithLabel(
                    label = "verifyStudentID/verifyStudentIdentity",
                    collectAction = { label, data ->
                        _verifyStudentIDState.resetWithLog(label, NetworkResult.Success(data))
                    },
                    catchAction = { label, error ->
                        _verifyStudentIDState.resetWithLog(label, networkErrorWithLog(Throwable("登录失败"),"验证失败"))
                    }
                )
            }

        }
    }

    fun refreshStudentCaptcha(){
        viewModelScope.launch(Dispatchers.IO) {
            _studentCaptcha.logicIfNotLoading {
                loginRepository.getVerifyCode()
                    .actionWithLabel(
                        "",
                        catchAction = { label,error ->
                            _studentCaptcha.resetWithLog(label, networkErrorWithLog(error,"刷新失败"))
                        },
                        collectAction = {   label, data ->
                            _studentCaptcha.resetWithLog(label,NetworkResult.Success(data.asImageBitmap()))
                        }
                    )

            }

        }
    }

    fun login(email: String,password: String,captcha:String){
        viewModelScope.launch (Dispatchers.Default){
            _loginCaptcha.logicIfNotLoading {
                loginRepository.login(email = email, password = password, captcha = captcha)
                    .actionWithLabel(
                        "label/label",
                        catchAction = { label, error ->
                            _loginState.resetWithLog(label, networkErrorWithLog(error,"登录失败"))
                        },
                        collectAction = { label, data ->
                            _loginState.resetWithLog(label,data.toNetworkResult())
                            if(data.code == 12){
                                kVault.set("token",data.data.toString())
                                rootAction.navigateFormAnywhereToMain()
                            }
                        }
                    )
            }
        }
    }

    fun getLoginCaptcha(email: String){
        viewModelScope.launchInDefault{
            loginRepository.getLoginCaptcha(email = email)
                .actionWithLabel(
                    "getLoginCaptcha/getLoginCaptcha",
                    collectAction = { label, data ->
                        _loginCaptcha.resetWithLog(label, data.toNetworkResult())
                    },
                    catchAction = { label, error ->
                        _loginCaptcha.resetWithLog(label, networkErrorWithLog(error,"申请失败,请稍后重试"))
                    }
                )
        }
    }

    private fun enterAuthor(){
        val token : String? = kVault.string(forKey = "token")
        token ?: return
        rootAction.navigateFormAnywhereToMain()
    }

}



