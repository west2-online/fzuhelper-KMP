package ui.compose.ModifierInformation

import data.modifer.ModifierAvatar
import data.modifer.ModifierData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.ModifierAvatarStatus
import repository.ModifierDataStatus
import repository.ModifierInformationRepository
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.reset

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
                    .catchWithMassage {
                        _modifierUserdataState.reset(NetworkResult.Error(Throwable("修改失败")))
                    }.collectWithMassage {
                        _modifierUserdataState.reset(it.toNetworkResult())
                    }
            }
        }
    }

    fun modifierUserAvatar(imageByteArray: ByteArray){
        viewModelScope.launch {
            _modifierAvatarState.logicIfNotLoading {
                repository.modifierAvatar(imageByteArray)
                    .catchWithMassage {
                        _modifierAvatarState.reset(NetworkResult.Error(Throwable("修改失败")))
                    }.collectWithMassage {
                        _modifierAvatarState.reset(it.toNetworkResult())
                    }
            }
        }
    }

}

fun ModifierData.toNetworkResult(): NetworkResult<String> {
    val response = ModifierDataStatus.values().find {
        it.value == this.code
    }
    response?.let {
        return when(it.value){
            2 -> NetworkResult.Success(it.describe)
            0,1 -> NetworkResult.Error(Throwable(it.describe))
            else -> NetworkResult.Error(Throwable("修改失败"))
        }
    }
    return NetworkResult.Error(Throwable("修改失败"))
}

fun ModifierAvatar.toNetworkResult(): NetworkResult<String> {
    val response = ModifierAvatarStatus.values().find {
        it.value == this.code
    }
    response?.let {
        return when(it.value){
            3 -> NetworkResult.Success("头像更新成功")
            else -> NetworkResult.Error(Throwable("修改失败"))
        }
    }
    return NetworkResult.Error(Throwable("修改失败"))
}