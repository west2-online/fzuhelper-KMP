package ui.util.flow

import config.BaseUrlConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

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
    this.collect {
        if(BaseUrlConfig.isDebug){
            println(it)
        }
        action(it)
    }
}