package util.flow

import config.BaseUrlConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

suspend fun <T>Flow<T>.catchWithMassage (
    label: String = "",
    action: suspend kotlinx.coroutines.flow.FlowCollector<T>.(kotlin.Throwable) -> kotlin.Unit,
): Flow<T> {
    return this.catch {
        if(BaseUrlConfig.isDebug){
            println("$label error : ${it.message.toString()}")
        }
        action.invoke(this,it)
    }
}

suspend fun <T>Flow<T>.collectWithMassage (
    label: String = "",
    action: suspend (T)->Unit,

){
    this.flowOn(Dispatchers.IO).collect {
        if(BaseUrlConfig.isDebug){
            println("$label collect : ${it} ${this.toString()}")
        }
        action(it)
    }
}

suspend fun <T>Flow<T>.actionWithLabel(
    label : String,
    catchAction: suspend kotlinx.coroutines.flow.FlowCollector<T>.(kotlin.Throwable) -> kotlin.Unit,
    collectAction:  suspend (T)->Unit
){
    this.catchWithMassage(
        label = label,
        action = catchAction
    ).collectWithMassage(
        label = label,
        action = collectAction
    )
}

fun CoroutineScope.launchInDefault(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
){
    this.launch (
        context, start, block
    )
}