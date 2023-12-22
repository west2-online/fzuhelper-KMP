package ui.compose.Person

import com.liftric.kvault.KVault
import data.Person.UserData.UserData
import data.Person.identity.PersonIdentityData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.PersonRepository
import ui.route.Route
import ui.route.RouteState
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.network.NetworkResult
import ui.util.network.reset

class PersonViewModel(
    private val personRepository: PersonRepository,
    private val routeState: RouteState,
    private val kVault: KVault
):ViewModel() {
    private val _userData = CMutableStateFlow(MutableStateFlow<NetworkResult<UserData>>(NetworkResult.UnSend()))
    val userData = _userData.asStateFlow()
    private val _personIdentityData = CMutableStateFlow(MutableStateFlow<NetworkResult<PersonIdentityData>>(NetworkResult.UnSend()))
    val identityData = _personIdentityData.asStateFlow()

    fun getUserData(){
        viewModelScope.launch(Dispatchers.Default) {
            personRepository.getUserData()
                .catchWithMassage {
                    _userData.reset(NetworkResult.Error(Throwable("获取失败")))
                }
                .collectWithMassage{ userData ->
                    UserDataResult.values().filter {
                        it.value == userData.code
                    }.let {
                        if(it.isNotEmpty()){
                            _userData.value = it[0].toNetworkResult(userData)
                            return@collectWithMassage
                        }
                    }
                }
        }
    }
    fun getIdentityData(){
        viewModelScope.launch(Dispatchers.Default) {
            personRepository.getUserIdentity()
                .catchWithMassage {
                    _personIdentityData.reset(NetworkResult.Error(Throwable("获取失败")))
                }
                .collectWithMassage{ userData ->
                    IdentityDataResult.values().filter {
                        it.value == userData.code
                    }.let {
                        if(it.isNotEmpty()){
                            _personIdentityData.value = it[0].toNetworkResult(userData)
                            return@collectWithMassage
                        }
                    }
                }
        }
    }
    fun navigateToModifierInformation(userId:Int,userData : UserData){
        userData.data?.let {
            routeState.navigateWithoutPop(
                Route.ModifierInformation(
                    userId = userId,
                    userData = it
                )
            )
        }

    }
}

enum class UserDataResult(val value:Int,val descrie:String){
    FailureToObtainPersonalInformationInUser(0,"获取信息失败"),
    SuccessToObtainPersonalInformationInUser(1,"获取成功"),
    FailedToGetTheMailboxInUser(2,"身份信息有误"),
    SerialNumberFailedInUser(3,"序列号失败"),
}

fun UserDataResult.toNetworkResult(userData: UserData):NetworkResult<UserData>{
    return when(this.value){
        0,2,3->NetworkResult.Error<UserData>(Throwable("获取失败"))
        1->NetworkResult.Success<UserData>(userData)
        else -> NetworkResult.Error<UserData>(Throwable("未知错误"))
    }
}

enum class IdentityDataResult(val value:Int,val descrie:String){
    IdentityAcquisitionFailed(0, "获取身份失败"),
    TheIdentityInformationWasSuccessfullyObtained(1, "获取身份成功")
}

fun IdentityDataResult.toNetworkResult(personIdentityData: PersonIdentityData):NetworkResult<PersonIdentityData>{
    return when(this.value){
        0 -> NetworkResult.Error<PersonIdentityData>(Throwable("获取失败"))
        1 -> NetworkResult.Success<PersonIdentityData>(personIdentityData)
        else -> NetworkResult.Error<PersonIdentityData>(Throwable("未知错误"))
    }
}