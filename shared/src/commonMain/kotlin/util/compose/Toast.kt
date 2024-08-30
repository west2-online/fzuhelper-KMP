package util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import util.network.NetworkResult
import util.network.toast

/**
 * toast基础类
 *
 * @property icon ImageVector? 用于显示的icon 目前未使用该参数
 * @property text String 用于显示的text
 * @property color Color 用于显示的颜色
 * @constructor
 */
data class ToastImp(val icon: ImageVector? = null, val text: String, val color: Color = Color.Cyan)

/**
 * toast基础类的进一步封装
 *
 * @property scope CoroutineScope toast使用的协程域
 * @property currentToast MutableState<ToastImp> 基于的ToastImp基础类
 * @property isShow MutableState<Boolean> 是否显示
 * @property mutex Mutex 用于控制显示的锁
 * @constructor
 */
class Toast(private val scope: CoroutineScope) {
  var currentToast = mutableStateOf<ToastImp>(ToastImp(null, "", Color.Cyan))
    private set

  var isShow = mutableStateOf(false)
    private set

  private val mutex = Mutex()

  /**
   * 显示toast 延迟2000毫米后会自动关闭toast
   *
   * @param toast ToastImp
   * @return Unit
   */
  private suspend fun show(toast: ToastImp): Unit {
    if (!mutex.isLocked) {
      mutex.withLock {
        try {
          currentToast.value = toast
          isShow.value = true
        } finally {
          delay(2000)
          isShow.value = false
        }
      }
    }
  }

  /**
   * 对show的协程封装
   *
   * @param toast ToastImp
   */
  private fun run(toast: ToastImp) {
    scope.launch { show(toast) }
  }

  /**
   * 添加普通的显示
   *
   * @param string String
   * @param color Color
   */
  fun addToast(string: String, color: Color = Color.Gray) {
    run(ToastImp(Icons.Filled.Refresh, string, color))
  }

  /**
   * 添加警告显示
   *
   * @param string String
   */
  fun addWarnToast(string: String) {
    run(ToastImp(Icons.Filled.Refresh, string, Color(248, 102, 95)))
  }
}

/**
 * 在compose函数中使用，即可自动使用该compose的协程域
 *
 * @param scope CoroutineScope
 * @return Toast
 */
@Composable
inline fun rememberToastState(scope: CoroutineScope = rememberCoroutineScope()): Toast = remember {
  Toast(scope)
}

/**
 * 将NetworkResult<String>类型绑定到toast，自动监听，自动toast
 *
 * @param networkResult1 State<NetworkResult<String>>?
 * @param networkResult2 State<NetworkResult<String>>?
 * @param networkResult3 State<NetworkResult<String>>?
 * @param networkResult4 State<NetworkResult<String>>?
 * @param networkResult5 State<NetworkResult<String>>?
 * @receiver Toast
 */
@Composable
inline fun Toast.toastBindNetworkResult(
  networkResult1: State<NetworkResult<String>>? = null,
  networkResult2: State<NetworkResult<String>>? = null,
  networkResult3: State<NetworkResult<String>>? = null,
  networkResult4: State<NetworkResult<String>>? = null,
  networkResult5: State<NetworkResult<String>>? = null,
) {
  if (networkResult1 != null) {
    LaunchedEffect(networkResult1.value.key) {
      networkResult1.value.toast(
        success = { this@toastBindNetworkResult.addToast(it) },
        error = { this@toastBindNetworkResult.addWarnToast(it.message.toString()) },
      )
    }
  }
  if (networkResult2 != null) {
    LaunchedEffect(networkResult2.value.key) {
      networkResult2.value.toast(
        success = { this@toastBindNetworkResult.addToast(it) },
        error = { this@toastBindNetworkResult.addWarnToast(it.message.toString()) },
      )
    }
  }
  if (networkResult3 != null) {
    LaunchedEffect(networkResult3.value.key) {
      networkResult3.value.toast(
        success = { this@toastBindNetworkResult.addToast(it) },
        error = { this@toastBindNetworkResult.addWarnToast(it.message.toString()) },
      )
    }
  }
  if (networkResult4 != null) {
    LaunchedEffect(networkResult4.value.key) {
      networkResult4.value.toast(
        success = { this@toastBindNetworkResult.addToast(it) },
        error = { this@toastBindNetworkResult.addWarnToast(it.message.toString()) },
      )
    }
  }
  if (networkResult5 != null) {
    LaunchedEffect(networkResult5.value.key) {
      networkResult5.value.toast(
        success = { this@toastBindNetworkResult.addToast(it) },
        error = { this@toastBindNetworkResult.addWarnToast(it.message.toString()) },
      )
    }
  }
}

/**
 * toast显示的ui，最好在Box中使用
 *
 * @param toast Toast
 * @param isParentStatusControl Boolean
 */
@Composable
fun EasyToast(toast: Toast = rememberToastState(), isParentStatusControl: Boolean = false) {
  AnimatedVisibility(
    toast.isShow.value,
    exit = slideOutVertically() { 0 } + shrinkVertically() + fadeOut(tween(5000)),
    enter = slideInVertically { 0 },
  ) {
    Box(
      modifier =
        Modifier.parentStatusControl(isParentStatusControl)
          .fillMaxWidth()
          .wrapContentHeight()
          .padding(10.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(toast.currentToast.value.color)
          .padding(10.dp)
    ) {
      toast.currentToast.value.let {
        Text(it.text, modifier = Modifier.align(Alignment.Center), textAlign = TextAlign.Center)
      }
    }
  }
}
