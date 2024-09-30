package util.flow

import config.BaseUrlConfig
import data.base.BaseResponseData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * 对flow的catch函数进行包装，在isDebug为true的情况下，用label输出日志
 *
 * @param label String
 * @param action [@kotlin.ExtensionFunctionType] SuspendFunction3<FlowCollector<T>,
 *   [@kotlin.ParameterName] String, Throwable, Unit>?
 * @return Flow<T>
 * @receiver Flow<T>
 */
@Deprecated("catchWithMessage2")
suspend fun <T> Flow<T>.catchWithMessage(
  label: String = "",
  action:
    (suspend kotlinx.coroutines.flow.FlowCollector<T>.(
      label: String, Throwable,
    ) -> Unit)? =
    null,
): Flow<T> {
  return this.catch {
    if (BaseUrlConfig.isDebug) {
      println("$label error : ${it.message.toString()}")
    }
    action?.invoke(this, label, it)
  }
}


suspend fun <T> Flow<BaseResponseData<T>>.catchWithMessage2(
  label: String = "",
  action:
  (suspend kotlinx.coroutines.flow.FlowCollector<BaseResponseData<T>>.(
    label: String, Throwable,
  ) -> Unit)? =
    null,
): Flow<BaseResponseData<T>> {
  return this.catch {
    if (BaseUrlConfig.isDebug) {
      println("$label error : ${it.message.toString()}")
    }
    action?.invoke(this, label, it)
  }
}

/**
 * 对flow的collect进行包装，在isDebug为true的情况下，用label输出结果
 *
 * @param label String
 * @param action SuspendFunction2<[@kotlin.ParameterName] String, [@kotlin.ParameterName] T, Unit>
 * @receiver Flow<T>
 */
@Deprecated("collectWithMessage2")
suspend fun <T> Flow<T>.collectWithMessage(
  label: String = "",
  action: suspend (label: String, data: T) -> Unit,
) {
  this.flowOn(Dispatchers.IO).collect {
    if (BaseUrlConfig.isDebug) {
      println("$label collect : ${it} ${this}")
    }
    action(label, it)
  }
}


suspend fun <T> Flow<BaseResponseData<T>>.collectWithMessage2(
  label: String = "",
  action: suspend (label: String, data: BaseResponseData<T>) -> Unit,
) {
  this.flowOn(Dispatchers.IO).collect {
    if (BaseUrlConfig.isDebug) {
      println("$label collect : ${it} ${this}")
    }
    action(label, it)
  }
}

/**
 * 集合 collectWithMessage 和 catchWithMessage，共享同一个label
 *
 * @param label String
 * @param catchAction [@kotlin.ExtensionFunctionType] SuspendFunction3<FlowCollector<T>,
 *   [@kotlin.ParameterName] String, [@kotlin.ParameterName] Throwable, Unit>
 * @param collectAction SuspendFunction2<[@kotlin.ParameterName] String, [@kotlin.ParameterName] T,
 *   Unit>
 * @receiver Flow<T>
 */
@Deprecated("actionWithLabel2")
suspend fun <T> Flow<T>.actionWithLabel(
  label: String,
  catchAction:
    suspend kotlinx.coroutines.flow.FlowCollector<T>.(
      label: String, error: Throwable,
    ) -> Unit,
  collectAction: suspend (label: String, data: T) -> Unit,
) {
  this.catchWithMessage(label = label, action = catchAction)
    .collectWithMessage(label = label, action = collectAction)
}


suspend fun <T> Flow<BaseResponseData<T>>.actionWithLabel2(
  label: String,
  catchAction:
  suspend kotlinx.coroutines.flow.FlowCollector<BaseResponseData<T>>.(
    label: String, error: Throwable,
  ) -> Unit,
  collectAction: suspend (label: String, data: BaseResponseData<T>) -> Unit,
) {
  this.catchWithMessage2(label = label, action = catchAction)
    .collectWithMessage2(label = label, action = collectAction)
}

/**
 * 开启一个运行在 CoroutineStart.DEFAULT 的协程
 *
 * @param start CoroutineStart
 * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
 * @receiver CoroutineScope
 */
fun CoroutineScope.launchInDefault(
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit,
) {
  this.launch(Dispatchers.Default, start, block)
}

/**
 * 开启一个运行在 CoroutineStart.IO 的协程
 *
 * @param start CoroutineStart
 * @param block [@kotlin.ExtensionFunctionType] SuspendFunction1<CoroutineScope, Unit>
 * @receiver CoroutineScope
 */
fun CoroutineScope.launchInIO(
  start: CoroutineStart = CoroutineStart.DEFAULT,
  block: suspend CoroutineScope.() -> Unit,
) {
  this.launch(Dispatchers.IO, start, block)
}
