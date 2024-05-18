package ui.compose.Person

import data.person.UserData.UserData
import data.person.identity.PersonIdentityData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.PersonRepository
import ui.root.RootAction
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 用户相关逻辑
 * @property personRepository PersonRepository
 * @property rootAction RootAction
 * @property _userData CMutableStateFlow<NetworkResult<UserData>>
 * @property userData StateFlow<NetworkResult<UserData>> 用户数据
 * @property _personIdentityData CMutableStateFlow<NetworkResult<PersonIdentityData>>
 * @property identityData StateFlow<NetworkResult<PersonIdentityData>> 用户身份数据
 * @constructor
 */
class PersonViewModel(
    private val personRepository: PersonRepository,
    private val rootAction: RootAction
):ViewModel() {
    private val _userData = CMutableStateFlow(MutableStateFlow<NetworkResult<UserData>>(
        NetworkResult.UnSend()))
    val userData = _userData.asStateFlow()
    private val _personIdentityData = CMutableStateFlow(MutableStateFlow<NetworkResult<PersonIdentityData>>(
        NetworkResult.UnSend()))
    val identityData = _personIdentityData.asStateFlow()

    /**
     * 根据id获取用户信息
     * @param id String? 为空时获取自身，不为空时获取他人
     */
    private fun getUserData(id:String?){
        id?:run {
            viewModelScope.launch(Dispatchers.Default) {
                personRepository.getUserDataMySelf()
                    .actionWithLabel(
                        "getUserData/getUserDataMySelf",
                        catchAction = {label, error ->
                            _userData.resetWithLog(label, networkErrorWithLog(error,"获取失败"))
                        },
                        collectAction = { label, data ->
                            _userData.resetWithLog(label,data.toNetworkResult())
                        }
                    )

            }
        }
        id?.let {
            viewModelScope.launch(Dispatchers.Default) {
                personRepository.getUserDataOther(it)
                    .actionWithLabel(
                        "getUserData/getUserDataMySelf",
                        catchAction = {label, error ->
                            _userData.resetWithLog(label, networkErrorWithLog(error,"获取失败"))
                        },
                        collectAction = { label, data ->
                            _userData.resetWithLog(label,data.toNetworkResult())
                        }
                    )
            }
        }
    }

    fun initUserData(id: String?){
        if (userData.value is NetworkResult.UnSend){
            getUserData(id)
        }
    }

    /**
     * 获取用户身份
     * @param id String? 为空时获取自身，不为空时获取他人
     */
    fun getIdentityData(id:String?){
        id?:run {
            viewModelScope.launch(Dispatchers.Default) {
                personRepository.getUserIdentityMySelf()
                    .actionWithLabel(
                        "getIdentityData/getUserIdentityMySelf",
                        collectAction = { label, data ->
                            _personIdentityData.resetWithLog(label,data.toNetworkResult())
                        },
                        catchAction = {label, error ->
                            _personIdentityData.resetWithLog(label, networkErrorWithLog(error,"获取失败"))
                        }
                    )
            }
        }
        id?.let {
            viewModelScope.launch(Dispatchers.Default) {
                personRepository.getUserIdentityOther(it)
                    .actionWithLabel(
                        "getIdentityData/getUserIdentityMySelf",
                        collectAction = { label, data ->
                            _personIdentityData.resetWithLog(label,data.toNetworkResult())
                        },
                        catchAction = {label, error ->
                            _personIdentityData.resetWithLog(label, networkErrorWithLog(error,"获取失败"))
                        }
                    )
            }
        }
    }

    /**
     * 更新用户数据
     * @param id String?
     */
    fun refreshUserData(id: String?) {
        getUserData(id)
    }
}


