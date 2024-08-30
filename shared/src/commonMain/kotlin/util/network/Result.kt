package util.network

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import annotation.ImportantFunction
import di.database
import di.globalScope
import kotlin.random.Random
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import util.flow.launchInDefault

/**
 * 网络结果的接口
 *
 * @param T
 * @property key MutableState<Int> 接口当前的key 用于刷新compose ui，即使两个结果完全相同
 * @property showToast Boolean 在绑定toast情况下，是否显示toast
 * @property hasDeal Boolean 是否已经被处理
 */
@Stable
interface NetworkResult<T> {
  val key: MutableState<Int>
  var showToast: Boolean
  var hasDeal: Boolean

  /**
   * 成功的网络结果
   *
   * @param T
   * @property dataForShow T 用于显示的结果
   * @property rawData T? 原始结果
   * @property showToast Boolean
   * @property key MutableState<Int>
   * @property hasDeal Boolean
   * @constructor
   */
  data class Success<T>(
    val dataForShow: T,
    val rawData: T? = null,
    override var showToast: Boolean = true,
    override val key: MutableState<Int> = mutableStateOf(0),
    override var hasDeal: Boolean = false,
  ) : NetworkResult<T>

  /**
   * 错误的显示结果
   *
   * @param T
   * @property errorForShow Throwable 用于显示的错误
   * @property rawError Throwable 源错误
   * @property showToast Boolean
   * @property key MutableState<Int>
   * @property hasDeal Boolean
   * @constructor
   */
  data class Error<T>(
    val errorForShow: Throwable,
    val rawError: Throwable,
    override var showToast: Boolean = true,
    override val key: MutableState<Int> = mutableStateOf(0),
    override var hasDeal: Boolean = false,
  ) : NetworkResult<T>

  /**
   * 进入加载，而且可以触发加载的逻辑
   *
   * @param T
   * @property showToast Boolean
   * @property key MutableState<Int>
   * @property hasDeal Boolean
   * @constructor
   */
  class LoadingWithAction<T>(
    override var showToast: Boolean = true,
    override val key: MutableState<Int> = mutableStateOf(0),
    override var hasDeal: Boolean = false,
  ) : NetworkResult<T>

  /**
   * 进入加载，不会触发加载的逻辑
   *
   * @param T
   * @property showToast Boolean
   * @property key MutableState<Int>
   * @property hasDeal Boolean
   * @constructor
   */
  class LoadingWithOutAction<T>(
    override var showToast: Boolean = true,
    override val key: MutableState<Int> = mutableStateOf(0),
    override var hasDeal: Boolean = false,
  ) : NetworkResult<T>

  /**
   * 没发送的逻辑，用于初始化定义
   *
   * @param T
   * @property showToast Boolean
   * @property key MutableState<Int>
   * @property hasDeal Boolean
   * @constructor
   */
  class UnSend<T>(
    override var showToast: Boolean = true,
    override val key: MutableState<Int> = mutableStateOf(0),
    override var hasDeal: Boolean = false,
  ) : NetworkResult<T>
}

/**
 * 用massage来创建 NetworkResult Error
 *
 * @param errorForShow String
 * @param error String
 * @return Error<T>
 */
fun <T> networkError(errorForShow: String, error: String) =
  NetworkResult.Error<T>(Throwable(errorForShow), Throwable(error))

/**
 * 用error来创建 NetworkResult Error
 *
 * @param errorForShow Throwable
 * @param error String
 * @return Error<T>
 */
fun <T> networkError(errorForShow: Throwable, error: String) =
  NetworkResult.Error<T>(Throwable(errorForShow), Throwable(error))

fun networkSuccess(success: String) = NetworkResult.Success<String>(success)

/**
 * 用网络请求返回的code来创建结果用于log
 *
 * @param errorCode Int
 * @param newDescribe String
 * @return Error<T>
 */
fun <T> networkErrorWithLog(errorCode: Int, newDescribe: String) =
  NetworkResult.Error<T>(
    rawError = Throwable("Error Code : $errorCode"),
    errorForShow = Throwable(newDescribe),
  )

/**
 * 用网络请求返回的code来创建结果用于log
 *
 * @param errorCode Int
 * @param newThrowable Throwable
 * @return Error<T>
 */
fun <T> networkErrorWithLog(errorCode: Int, newThrowable: Throwable) =
  NetworkResult.Error<T>(
    rawError = Throwable("Error Code : $errorCode"),
    errorForShow = newThrowable,
  )

/**
 * 用错误来创建结果用于log
 *
 * @param error Throwable
 * @param newDescribe String
 * @return Error<T>
 */
fun <T> networkErrorWithLog(error: Throwable, newDescribe: String) =
  NetworkResult.Error<T>(rawError = error, errorForShow = Throwable(newDescribe))

/**
 * 根据网络结果的类型渲染不同的ui 在Crossfade中渲染
 *
 * @param success [@androidx.compose.runtime.Composable] Function1<T, Unit>?
 * @param error [@androidx.compose.runtime.Composable] Function1<Throwable, Unit>?
 * @param loading [@androidx.compose.runtime.Composable] Function0<Unit>?
 * @param unSend [@androidx.compose.runtime.Composable] Function0<Unit>?
 * @param content [@androidx.compose.runtime.Composable] Function0<Unit>
 * @param modifier Modifier
 * @receiver State<NetworkResult<T>>
 */
@Composable
fun <T> State<NetworkResult<T>>.CollectWithContent(
  success: (@Composable (T) -> Unit)? = null,
  error: (@Composable (Throwable) -> Unit)? = null,
  loading: (@Composable () -> Unit)? = null,
  unSend: (@Composable () -> Unit)? = null,
  content: @Composable () -> Unit = {},
  modifier: Modifier = Modifier.fillMaxSize(),
) {
  Crossfade(this.value, modifier = modifier) {
    when (it) {
      is NetworkResult.Success<T> -> {
        if (success == null) {
          content.invoke()
        } else {
          success.invoke(it.dataForShow)
        }
      }
      is NetworkResult.Error<T> -> {

        if (error == null) {
          content.invoke()
        } else {
          error.invoke(it.errorForShow)
        }
      }
      is NetworkResult.LoadingWithAction<T> -> {
        if (loading == null) {
          content.invoke()
        } else {
          loading.invoke()
        }
      }
      is NetworkResult.UnSend<T> -> {
        if (unSend == null) {
          content.invoke()
        } else {
          unSend.invoke()
        }
      }

      is NetworkResult.LoadingWithOutAction<T> -> {
        if (loading == null) {
          content.invoke()
        } else {
          loading.invoke()
        }
      }
    }
  }
}

/**
 * 根据网络结果的类型渲染不同的ui 在Box中渲染
 *
 * @param success [@androidx.compose.runtime.Composable] [@kotlin.ExtensionFunctionType]
 *   Function2<BoxScope, T, Unit>?
 * @param error [@androidx.compose.runtime.Composable] [@kotlin.ExtensionFunctionType]
 *   Function2<BoxScope, Throwable, Unit>?
 * @param loading [@androidx.compose.runtime.Composable] [@kotlin.ExtensionFunctionType]
 *   Function1<BoxScope, Unit>?
 * @param unSend [@androidx.compose.runtime.Composable] [@kotlin.ExtensionFunctionType]
 *   Function1<BoxScope, Unit>?
 * @param content [@androidx.compose.runtime.Composable] [@kotlin.ExtensionFunctionType]
 *   Function1<BoxScope, Unit>
 * @param modifier Modifier
 * @receiver State<NetworkResult<T>>
 */
@Composable
fun <T> State<NetworkResult<T>>.CollectWithContentInBox(
  success: (@Composable BoxScope.(T) -> Unit)? = null,
  error: (@Composable BoxScope.(Throwable) -> Unit)? = null,
  loading: (@Composable BoxScope.() -> Unit)? = null,
  unSend: (@Composable BoxScope.() -> Unit)? = null,
  content: @Composable BoxScope.() -> Unit = {},
  modifier: Modifier = Modifier,
) {
  Crossfade(this.value, modifier = modifier) {
    Box(modifier = Modifier.fillMaxSize()) {
      when (it) {
        is NetworkResult.Success<T> -> {
          if (success == null) {
            content.invoke(this)
          } else {
            success.invoke(this, it.dataForShow)
          }
        }
        is NetworkResult.Error<T> -> {

          if (error == null) {
            content.invoke(this)
          } else {
            error.invoke(this, it.errorForShow)
          }
        }
        is NetworkResult.LoadingWithAction<T> -> {
          if (loading == null) {
            content.invoke(this)
          } else {
            loading.invoke(this)
          }
        }
        is NetworkResult.UnSend<T> -> {
          if (unSend == null) {
            content.invoke(this)
          } else {
            unSend.invoke(this)
          }
        }

        is NetworkResult.LoadingWithOutAction<T> -> {
          if (loading == null) {
            content.invoke(this)
          } else {
            loading.invoke(this)
          }
        }
      }
    }
  }
}

suspend fun <T> NetworkResult<T>.getNetwork(
  success: Boolean = true,
  error: Boolean = true,
  loading: Boolean = false,
  unSend: Boolean = true,
  block: suspend () -> Unit,
) {
  when (this) {
    is NetworkResult.Success<T> -> {
      if (success) {
        block.invoke()
      }
    }
    is NetworkResult.Error<T> -> {
      if (error) {
        block.invoke()
      }
    }
    is NetworkResult.LoadingWithAction<T> -> {
      if (loading) {
        block.invoke()
      }
    }
    is NetworkResult.UnSend<T> -> {
      if (unSend) {
        block.invoke()
      }
    }

    is NetworkResult.LoadingWithOutAction -> {}
  }
}

fun <T> T.logicWithNullCheck(isNull: () -> Unit = {}, isNotNull: (T) -> Unit = {}) {
  if (this == null) {
    isNull.invoke()
  } else {
    isNotNull.invoke(this)
  }
}

@Composable
fun <T> T.logicWithNullCheckInCompose(
  isNull: @Composable () -> Unit = {},
  isNotNull: @Composable (T) -> Unit = {},
) {
  if (this == null) {
    isNull.invoke()
  } else {
    isNotNull.invoke(this)
  }
}

/**
 * 重新设置网络结果，触发ui更新，同时触发日志
 *
 * @param logLabel String
 * @param newValue NetworkResult<T>
 * @receiver MutableStateFlow<NetworkResult<T>>
 */
@ImportantFunction
suspend fun <T> MutableStateFlow<NetworkResult<T>>.resetWithLog(
  logLabel: String,
  newValue: NetworkResult<T>,
) {
  if (newValue is NetworkResult.Error) {
    globalScope.launchInDefault {
      var errorMassage = ""
      errorMassage += "$logLabel >>> ${newValue.rawError.message.toString()}"
      var cause = newValue.rawError.cause
      while (cause != null) {
        errorMassage += " --> ${ newValue.rawError.message.toString()}"
        cause = cause.cause
      }
      database.networkLogQueries.insertNetworkErrorLog(
        time = Clock.System.now().toString().toEasyTimeWithSecond(),
        error = errorMassage,
      )
    }
  }
  val oldKey = this.value.key.value
  val newKey = Random(0).nextInt(oldKey + 10, (oldKey + 100))
  this.value = newValue.apply { key.value = newKey }
  delay(1500)
  this.value.showToast = false
}

/**
 * 重新设置网络结果，触发ui更新，不会触发日志
 *
 * @param newValue NetworkResult<T>
 * @receiver MutableStateFlow<NetworkResult<T>>
 */
@ImportantFunction
suspend fun <T> MutableStateFlow<NetworkResult<T>>.resetWithoutLog(newValue: NetworkResult<T>) {
  val oldKey = this.value.key.value
  val newKey = Random(0).nextInt(oldKey + 10, (oldKey + 100))
  this.value = newValue.apply { key.value = newKey }
  delay(1500)
  this.value.showToast = false
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.intoLoading() {
  val oldKey = this.value.key.value
  val newKey = Random(0).nextInt(0, (oldKey + 10))
  this.value = NetworkResult.LoadingWithAction<T>().apply { key.value = newKey }
  delay(1500)
  this.value.showToast = false
}

/**
 * 将网络结果设置为加载中
 *
 * @receiver MutableStateFlow<NetworkResult<T>>
 */
suspend fun <T> MutableStateFlow<NetworkResult<T>>.loading() {
  this.resetWithoutLog(NetworkResult.LoadingWithAction())
}

/**
 * 将网络结果设置为未发送
 *
 * @receiver MutableStateFlow<NetworkResult<T>>
 */
suspend fun <T> MutableStateFlow<NetworkResult<T>>.unSend() {
  this.resetWithoutLog(NetworkResult.UnSend())
}

/**
 * 如果网络结果不是加载中就执行的逻辑，主要防止连点
 *
 * @param preAction SuspendFunction0<Unit>
 * @param block SuspendFunction0<Unit>
 * @receiver MutableStateFlow<NetworkResult<T>>
 */
suspend fun <T> MutableStateFlow<NetworkResult<T>>.logicIfNotLoading(
  preAction: suspend () -> Unit = {},
  block: suspend () -> Unit,
) {
  preAction.invoke()
  if (this.value !is NetworkResult.LoadingWithAction) {
    this.resetWithoutLog(NetworkResult.LoadingWithAction())
    block.invoke()
  }
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.logicIfUnSendInSuspend(
  preAction: suspend () -> Unit = {},
  block: suspend () -> Unit,
) {
  preAction.invoke()
  if (this.value is NetworkResult.UnSend) {
    block.invoke()
  }
}

fun <T> MutableStateFlow<NetworkResult<T>>.logicIfUnSend(block: () -> Unit) {
  if (this.value is NetworkResult.UnSend) {
    block.invoke()
  }
}

/**
 * 对网络结果的处理，无视是否已处理，不会更改结果是否已处理，可以处理无数次
 *
 * @param success Function1<T, Unit>?
 * @param error Function1<Throwable, Unit>?
 * @param unSend Function0<Unit>?
 * @param loading Function0<Unit>?
 * @receiver NetworkResult<T>
 */
fun <T> NetworkResult<T>.logicWithTypeWithoutLimit(
  success: ((T) -> Unit)? = null,
  error: ((Throwable) -> Unit)? = null,
  unSend: (() -> Unit)? = null,
  loading: (() -> Unit)? = null,
) {
  when (this) {
    is NetworkResult.Success<T> -> {
      success?.invoke(this.dataForShow)
    }
    is NetworkResult.Error<T> -> {
      error?.invoke(this.errorForShow)
    }
    is NetworkResult.LoadingWithAction<T> -> {
      loading?.invoke()
    }
    is NetworkResult.UnSend<T> -> {
      unSend?.invoke()
    }
    is NetworkResult.LoadingWithOutAction<T> -> {
      loading?.invoke()
    }
  }
}

/**
 * 对网络结果的处理，会判断是否已处理，会更改结果是否已处理，只能处理一次
 *
 * @param success Function1<T, Unit>?
 * @param error Function1<Throwable, Unit>?
 * @param unSend Function0<Unit>?
 * @param loading Function0<Unit>?
 * @receiver NetworkResult<T>
 */
fun <T> NetworkResult<T>.logicWithTypeWithLimit(
  success: ((T) -> Unit)? = null,
  error: ((Throwable) -> Unit)? = null,
  unSend: (() -> Unit)? = null,
  loading: (() -> Unit)? = null,
) {
  if (!this.hasDeal) {
    this.hasDeal = true
    when (this) {
      is NetworkResult.Success<T> -> {
        success?.invoke(this.dataForShow)
      }
      is NetworkResult.Error<T> -> {
        error?.invoke(this.errorForShow)
      }
      is NetworkResult.LoadingWithAction<T> -> {
        loading?.invoke()
      }
      is NetworkResult.UnSend<T> -> {
        unSend?.invoke()
      }
      is NetworkResult.LoadingWithOutAction<T> -> {
        loading?.invoke()
      }
    }
  }
}

/**
 * 对网络结果的toast处理
 *
 * @param success Function1<T, Unit>?
 * @param error Function1<Throwable, Unit>?
 * @param unSend Function0<Unit>?
 * @param loading Function0<Unit>?
 * @receiver NetworkResult<T>
 */
suspend fun <T> NetworkResult<T>.toast(
  success: ((T) -> Unit)? = null,
  error: ((Throwable) -> Unit)? = null,
  unSend: (() -> Unit)? = null,
  loading: (() -> Unit)? = null,
) {
  if (this.showToast) {
    this.logicWithTypeWithoutLimit(
      success = success,
      error = error,
      unSend = unSend,
      loading = loading,
    )
  }
}
