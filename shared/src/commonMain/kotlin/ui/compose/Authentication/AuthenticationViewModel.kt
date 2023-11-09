package ui.compose.Authentication

import androidx.compose.ui.graphics.ImageBitmap
import asImageBitmap
import data.LoginRepository
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

    private val _registerState = CMutableStateFlow(MutableStateFlow<NetworkResult<Int>>(NetworkResult.UnSend()))
    val registerState = _registerState.asStateFlow()


    private val _studentCaptcha = CMutableStateFlow(MutableStateFlow<NetworkResult<ImageBitmap>>(NetworkResult.UnSend()))
    val studentCaptcha = _studentCaptcha.asStateFlow()


    fun getCaptcha(email:String){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.getCaptcha(email = "")
                .catch {
                    _captcha.value = NetworkResult.Error(it)
                }
                .collect{
                    _captcha.value = NetworkResult.Success(it.data)
                }
        }
    }

    fun register(email:String,password:String,captcha:String){
        viewModelScope.launch (Dispatchers.Default){
            loginRepository.register(email = email, password = password, captcha = captcha)
                .catch {
                    _registerState.value = NetworkResult.Error(it)
                }.collect{
                    _registerState.value = NetworkResult.Success(it.code)
                }
        }
    }

    fun verifyStudentID( studentCode : String, studentPassword:String,captcha:String){
        viewModelScope.launch (Dispatchers.IO){
            loginRepository.verifyStudentIdentity(
                userName = studentCode, password = studentPassword ,captcha = captcha
            ).catch {
                println(it.toString())
            }
            .collect{
                println(it.toString())
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

}