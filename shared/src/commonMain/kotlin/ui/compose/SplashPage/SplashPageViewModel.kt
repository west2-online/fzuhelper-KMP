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

/**
 * 开屏页的逻辑
 *
 * @property splashRepository SplashRepository
 * @property kVault KVault
 * @property _imageState CMutableStateFlow<NetworkResult<String>>
 * @property imageState StateFlow<NetworkResult<String>>
 * @constructor
 */
class SplashPageViewModel(
  private val splashRepository: SplashRepository,
  private val kVault: KVault,
) : ViewModel() {
  private val _imageState =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val imageState = _imageState.asStateFlow()

  /** Get splash page image 获取开屏页界面结果 */
  fun getSplashPageImage() {
    viewModelScope.launch {
      _imageState.logicIfNotLoading {
        splashRepository
          .getOpenImage()
          .actionWithLabel(
            "getSplashPageImage/getOpenImage",
            catchAction = { label, error ->
              _imageState.resetWithLog(label, networkErrorWithLog(error, "获取错误"))
            },
            collectAction = { label, data ->
              _imageState.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }
}
