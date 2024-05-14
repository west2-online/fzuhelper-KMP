package util.kValue

import com.liftric.kvault.KVault
import di.globalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import util.flow.launchInIO

class KValueStringDate (
    val key: String,
    private val data: MutableStateFlow<String?>,
    private val kValue:KVault
){
    val currentValue = data.asStateFlow()
    private suspend fun  store(state: StateFlow<String?>, key:String){
        state.collect{
            println(it)
            state.value?.let { it1 -> kValue.set(key, it1) }
        }
    }
    suspend fun setValue(newValue:String?){
        data.emit(newValue)
    }
    init {
        globalScope.launchInIO {
            data.value = kValue.string(key)
            store(currentValue,key)
        }
    }
}

class KValueIntDate (
    val key: String,
    private val data: MutableStateFlow<Int?>,
    private val kValue:KVault
){
    val currentValue = data.asStateFlow()
    private suspend fun  store(state: StateFlow<Int?>, key:String){
        state.collect{
            state.value?.let { it1 -> kValue.set(key, it1) }
        }
    }
    suspend fun setValue(newValue:Int?){
        data.emit(newValue)
    }
    init {
        globalScope.launchInIO {
            data.value = kValue.int(key)
            store(currentValue,key)
        }
    }
}