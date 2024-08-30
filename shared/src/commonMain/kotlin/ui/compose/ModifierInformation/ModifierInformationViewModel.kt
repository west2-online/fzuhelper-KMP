package ui.compose.ModifierInformation

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.ModifierInformationRepository
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 更新用户逻辑
 *
 * @property repository ModifierInformationRepository
 * @property _modifierUserdataState CMutableStateFlow<NetworkResult<String>>
 * @property modifierUserdataState StateFlow<NetworkResult<String>> 更新用户信息的结果
 * @property _modifierAvatarState CMutableStateFlow<NetworkResult<String>>
 * @property modifierAvatarState StateFlow<NetworkResult<String>> 更新用户头像的结果
 * @constructor
 */
class ModifierInformationViewModel(val repository: ModifierInformationRepository) : ViewModel() {

  private val _modifierUserdataState =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val modifierUserdataState = _modifierUserdataState.asStateFlow()

  private val _modifierAvatarState =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val modifierAvatarState = _modifierAvatarState.asStateFlow()

  /**
   * 更新用户的信息
   *
   * @param username String
   * @param age String
   * @param grade String
   * @param location String
   */
  fun modifierUserdata(username: String, age: String, grade: String, location: String) {
    viewModelScope.launch {
      _modifierUserdataState.logicIfNotLoading {
        repository
          .modifierUserData(username, age, grade, location)
          .actionWithLabel(
            "modifierUserdata/modifierUserData",
            catchAction = { label, error ->
              _modifierUserdataState.resetWithLog(label, networkErrorWithLog(error, "修改失败"))
            },
            collectAction = { label, data ->
              _modifierUserdataState.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }

  /**
   * 更新用户的头像
   *
   * @param imageByteArray ByteArray
   */
  fun modifierUserAvatar(imageByteArray: ByteArray) {
    viewModelScope.launch {
      _modifierAvatarState.logicIfNotLoading {
        repository
          .modifierAvatar(imageByteArray)
          .actionWithLabel(
            "modifierUserAvatar/modifierUserAvatar",
            catchAction = { label, error ->
              _modifierAvatarState.resetWithLog(label, networkErrorWithLog(error, "修改失败"))
            },
            collectAction = { label, data ->
              _modifierAvatarState.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }
}
