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

    fun getUserData(id:String?){
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
}


