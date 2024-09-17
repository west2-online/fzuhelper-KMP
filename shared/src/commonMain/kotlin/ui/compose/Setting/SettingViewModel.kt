package ui.compose.Setting

import dao.UndergraduateKValueAction
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import di.Jwch
import kotlinx.coroutines.flow.MutableStateFlow
import util.flow.launchInDefault
import util.flow.launchInIO
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkError
import util.network.networkSuccess
import util.network.resetWithoutLog

/**
 * 设置的逻辑
 *
 * @property kValueAction UndergraduateKValueAction
 * @property jwch Jwch
 * @property signInStatus MutableStateFlow<NetworkResult<String>>
 * @constructor
 */
class SettingViewModel(
  val kValueAction: UndergraduateKValueAction,
  val jwch: Jwch,
) : ViewModel() {
  val signInStatus = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())

  /**
   * 验证账号
   *
   * @param userName String
   * @param password String
   */
  fun verifyTheAccount(userName: String, password: String, loginType: Int) {
    viewModelScope.launchInDefault {
      signInStatus.logicIfNotLoading {
        jwch.verifyYourAccount(
          userName,
          password,
          failAction = {
            when (it) {
              Jwch.VerifyYourAccountError.ValidationFailed -> {
                signInStatus.resetWithoutLog(networkError("验证失败,请稍后重试", "验证失败"))
              }

              Jwch.VerifyYourAccountError.LoginFailed -> {
                signInStatus.resetWithoutLog(networkError("登录失败,请稍后重试", ""))
              }
            }
          },
          success = {
            signInStatus.resetWithoutLog(networkSuccess("登录成功"))
            kValueAction.schoolUserName.setValue(userName)
            kValueAction.schoolPassword.setValue(password)
            kValueAction.loginType.setValue(loginType)
          },
        )
      }
    }
  }

  fun clearAccount() {
    viewModelScope.launchInIO {
      kValueAction.schoolUserName.setValue(null)
      kValueAction.schoolPassword.setValue(null)
    }
  }
}
