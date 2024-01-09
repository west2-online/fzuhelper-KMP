package ui.util.flow

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
    action: suspend kotlinx.coroutines.flow.FlowCollector<T>.(kotlin.Throwable) -> kotlin.Unit
): Flow<T> {
    return this.catch {
        if(BaseUrlConfig.isDebug){
            println(it.message.toString())
        }
        action.invoke(this,it)
    }
}

suspend fun <T>Flow<T>.collectWithMassage (
    action: suspend (T)->Unit
){
    this.flowOn(Dispatchers.IO).collect {
        if(BaseUrlConfig.isDebug){
            println(it)
        }
        action(it)
    }
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