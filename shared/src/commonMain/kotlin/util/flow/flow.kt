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

suspend fun <T>Flow<T>.catchWithMassage (
    label: String = "",
    action:  (suspend kotlinx.coroutines.flow.FlowCollector<T>.(label:String,kotlin.Throwable) -> kotlin.Unit)? = null,
): Flow<T> {
    return this.catch {
        if(BaseUrlConfig.isDebug){
            println("$label error : ${it.message.toString()}")
        }
        action?.invoke(this,label,it)
    }
}

suspend fun <T>Flow<T>.collectWithMassage (
    label: String = "",
    action: suspend (label: String,data : T)->Unit,
){
    this.flowOn(Dispatchers.IO).collect {
        if(BaseUrlConfig.isDebug){
            println("$label collect : ${it} ${this.toString()}")
        }
        action(label,it)
    }
}

suspend fun <T>Flow<T>.actionWithLabel(
    label : String,
    catchAction: suspend kotlinx.coroutines.flow.FlowCollector<T>.(label:String,error:kotlin.Throwable) -> kotlin.Unit,
    collectAction:  suspend (label:String,data : T)->Unit
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
    context: CoroutineContext = Dispatchers.Default,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
){
    this.launch (
        context, start, block
    )
}

fun CoroutineScope.launchInIO(
    context: CoroutineContext = Dispatchers.IO,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
){
    this.launch (
        context, start, block
    )
}