package ui.compose.PERSON

import com.liftric.kvault.KVault
import data.Person.UserData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import repository.PersonRepository
import token
import ui.route.RouteState
import ui.util.network.NetworkResult
import ui.util.network.reset

class PersonViewModel(
    private val personRepository: PersonRepository,
    private val routeState: RouteState,
    private val kVault: KVault
):ViewModel() {
    private val _userData = CMutableStateFlow(MutableStateFlow<NetworkResult<UserData>>(NetworkResult.UnSend()))
    val userData = _userData.asStateFlow()

    fun getUserData(){
        viewModelScope.launch(Dispatchers.Default) {
            kVault.token(
                routeState = routeState,
            ){ token ->
                personRepository.getUserData(token)
                   .catch {
                       println(it.message)
                       _userData.reset(NetworkResult.Error(Throwable("获取失败1")))
                   }
                   .collect{ userData ->
                       UserDataResult.values().filter {
                           it.value == userData.code
                       }.let {
                           if(it.isNotEmpty()){
                               _userData.value = it[0].toNetworkResult(userData)
                               return@collect
                           }
                       }
                   }
            }
        }
    }
}

enum class UserDataResult(val value:Int,val descrie:String){
    FailureToObtainPersonalInformationInUser(0,"获取信息失败"),
    SuccessToObtainPersonalInformationInUser(1,"获取成功"),
    FailedToGetTheMailboxInUser(2,"身份信息有误"),
    SerialNumberFailedInUser(3,"序列号失败"),
}

fun UserDataResult.toNetworkResult(userData:UserData):NetworkResult<UserData>{
    return when(this.value){
        0,2,3->NetworkResult.Error<UserData>(Throwable("获取失败"))
        1->NetworkResult.Success<UserData>(userData)
        else -> NetworkResult.Error<UserData>(Throwable("未知错误"))
    }
}