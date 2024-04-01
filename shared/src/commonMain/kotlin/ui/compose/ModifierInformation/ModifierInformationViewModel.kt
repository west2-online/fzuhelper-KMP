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

class ModifierInformationViewModel(val repository : ModifierInformationRepository) :ViewModel(){

    private val _modifierUserdataState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val modifierUserdataState = _modifierUserdataState.asStateFlow()

    private val _modifierAvatarState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val modifierAvatarState = _modifierAvatarState.asStateFlow()

    fun modifierUserdata(username:String, age:String, grade:String, location:String){
        viewModelScope.launch {
            _modifierUserdataState.logicIfNotLoading{
                repository.modifierUserData(username, age, grade, location)
                    .actionWithLabel(
                        "modifierUserdata/modifierUserData",
                        catchAction = { label, error ->
                            _modifierUserdataState.resetWithLog(label,networkErrorWithLog(error,"修改失败"))
                        },
                        collectAction = { label, data ->
                            _modifierUserdataState.resetWithLog(label,data.toNetworkResult())
                        }
                    )
            }
        }
    }


    fun modifierUserAvatar(imageByteArray: ByteArray){
        viewModelScope.launch {
            _modifierAvatarState.logicIfNotLoading {
                repository.modifierAvatar(imageByteArray)
                    .actionWithLabel(
                        "modifierUserAvatar/modifierUserAvatar",
                        catchAction = { label, error ->
                            _modifierAvatarState.resetWithLog(label,networkErrorWithLog(error,"修改失败"))
                        },
                        collectAction = { label, data ->
                            _modifierAvatarState.resetWithLog(label,data.toNetworkResult())
                        }
                    )
            }
        }
    }
}


