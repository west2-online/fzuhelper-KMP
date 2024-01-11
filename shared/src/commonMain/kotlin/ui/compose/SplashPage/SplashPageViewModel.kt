package ui.compose.SplashPage

import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.SplashRepository
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.network.NetworkResult
import ui.util.network.reset

class SplashPageViewModel(
    private val splashRepository: SplashRepository,
    private val kVault: KVault,
):ViewModel() {
    private val _imageState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val imageState = _imageState.asStateFlow()

    fun getSplashPageImage(){
        viewModelScope.launch {
            splashRepository.getOpenImage()
                .catchWithMassage {
                    _imageState.reset(
                        NetworkResult.Error(Throwable("获取错误"))
                    )
                }
                .collectWithMassage{ splashData ->
                    GetResult.values().filter {
                        it.value == splashData.code
                    }.let { listGetResult ->
                        if(listGetResult.isEmpty()){
                            _imageState.reset(
                                NetworkResult.Error(Throwable("加载失败"))
                            )
                        }else{
                            _imageState.reset(listGetResult[0].toNetWorkResult(splashData.data))
                        }
                    }
                }
        }

    }
}

enum class GetResult(val value: Int, val description: String) {
    GetSUCCESS(0, "操作成功"),
    GetError(1, "发生错误"),
}
fun GetResult.toNetWorkResult(data: String?): NetworkResult<String> {
    return when(this.value){
        1 -> NetworkResult.Error<String>(Throwable(this.description))
        0 -> NetworkResult.Success<String>(data.toString())
        else -> NetworkResult.Error<String>(Throwable("加载失败"))
    }
}
