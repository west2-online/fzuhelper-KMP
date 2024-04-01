package ui.compose.SplashPage

import com.liftric.kvault.KVault
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.SplashRepository
import util.flow.actionWithLabel
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

class SplashPageViewModel(
    private val splashRepository: SplashRepository,
    private val kVault: KVault,
):ViewModel() {
    private val _imageState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val imageState = _imageState.asStateFlow()

    fun getSplashPageImage(){
        viewModelScope.launch {
            _imageState.logicIfNotLoading {
                splashRepository.getOpenImage()
                    .actionWithLabel(
                        "getSplashPageImage/getOpenImage",
                        catchAction = { label, error ->
                            _imageState.resetWithLog(label, networkErrorWithLog(error,"获取错误"))
                        },
                        collectAction = { label, data ->
                            _imageState.resetWithLog(label, data.toNetworkResult())
                        }
                    )
            }

        }

    }
}

