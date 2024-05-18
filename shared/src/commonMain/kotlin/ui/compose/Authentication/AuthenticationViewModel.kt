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

/**
 * 登录注册的相关逻辑
 * @property loginRepository LoginRepository
 * @property kVault KVault
 * @property rootAction RootAction
 * @property _registerCaptcha CMutableStateFlow<NetworkResult<String>>
 * @property registerCaptcha StateFlow<NetworkResult<String>> 获取注册验证码的结果
 * @property _registerState CMutableStateFlow<NetworkResult<String>>
 * @property registerState StateFlow<NetworkResult<String>> 注册的结果
 * @property _studentCaptcha CMutableStateFlow<NetworkResult<ImageBitmap>>
 * @property studentCaptcha StateFlow<NetworkResult<ImageBitmap>> 获取教务处验证码的结果
 * @property _verifyStudentIDState CMutableStateFlow<NetworkResult<TokenData>>
 * @property verifyStudentIDState StateFlow<NetworkResult<TokenData>> 验证学生教务处身份的结果
 * @property _loginState CMutableStateFlow<NetworkResult<String>>
 * @property loginState StateFlow<NetworkResult<String>> 登录的结果
 * @property _loginCaptcha CMutableStateFlow<NetworkResult<String>>
 * @property loginCaptcha StateFlow<NetworkResult<String>> 登录的验证码发送结果
 * @constructor
 */
class AuthenticationViewModel(
    private val loginRepository: LoginRepository,
    private val kVault: KVault,
    private val rootAction: RootAction
) : ViewModel() {
    private val _registerCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val registerCaptcha = _registerCaptcha.asStateFlow()

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

    /**
     * 获取注册验证码
     * @param email String
     */
    fun getRegisterCaptcha(email:String){
        viewModelScope.launch{
            _registerCaptcha.logicIfNotLoading {
                loginRepository.getRegisterCaptcha(email = email)
                    .actionWithLabel(
                        "getRegisterCaptcha/getRegisterCaptcha",
                        catchAction = {label, error ->
                            _registerCaptcha.resetWithLog(label,NetworkResult.Error(error,Throwable("申请失败,请稍后重试")))
                        },
                        collectAction = { label, data ->
                            _registerCaptcha.resetWithLog(label,data.toNetworkResult())
                        }
                    )
            }

        }
    }

    /**
     * 注册
     * @param email String
     * @param password String
     * @param captcha String
     * @param studentCode String
     * @param studentPassword String
     */
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

    /**
     * 验证学生身份
     * @param studentCode String
     * @param studentPassword String
     * @param captcha String
     */
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

    /**
     * Refresh student captcha
     * 刷新教务处的验证码
     */
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

    /**
     * 登录futalk
     * @param email String
     * @param password String
     * @param captcha String
     */
    fun login(email: String,password: String,captcha:String){
        viewModelScope.launch (Dispatchers.Default){
            _loginState.logicIfNotLoading {
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
                                rootAction.navigateFormLoginToMain()
                            }
                        }
                    )
            }
        }
    }

    /**
     * 获取登录的验证码
     * @param email String
     */
    fun getLoginCaptcha(email: String){
        viewModelScope.launchInDefault{
            loginRepository.getLoginCaptcha(email = email)
                .actionWithLabel(
                    "getLoginCaptcha/getLoginCaptcha",
                    collectAction
                    = { label, data ->
                        _loginCaptcha.resetWithLog(label, data.toNetworkResult())
                    },
                    catchAction = { label, error ->
                        _loginCaptcha.resetWithLog(label, networkErrorWithLog(error,"申请失败,请稍后重试"))
                    }
                )
        }
    }
}



