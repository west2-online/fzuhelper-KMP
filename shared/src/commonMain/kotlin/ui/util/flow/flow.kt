package ui.util.flow

import config.BaseUrlConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn

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