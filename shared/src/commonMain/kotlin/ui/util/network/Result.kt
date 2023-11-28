package ui.util.network

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlin.random.Random

@Stable
interface NetworkResult<T> {
    val key: MutableState<Int>

    data class Success<T>(
        val data:T,
        override val key: MutableState<Int> = mutableStateOf(0)
    ) : NetworkResult<T>

    data class Error<T>(
        val error: Throwable,
        override val key: MutableState<Int> = mutableStateOf(0)
    ):NetworkResult<T>

    class Loading<T>(override val key: MutableState<Int> = mutableStateOf(0)):NetworkResult<T>

    class UnSend<T>(override val key: MutableState<Int> = mutableStateOf(0)) : NetworkResult<T>

}

@Composable
fun <T> State<NetworkResult<T>>.CollectWithContent(
    success : (@Composable (T)->Unit)? = null,
    error  : (@Composable (Throwable)->Unit)? = null,
    loading : (@Composable ()->Unit)? = null,
    unSend : (@Composable ()->Unit)? = null,
    content :@Composable ()->Unit = {},
    modifier: Modifier = Modifier
){
    Crossfade(
        this.value,
        modifier = modifier
    ){
        when(it){
            is NetworkResult.Success<T>->{
                if (success == null){
                    content.invoke()
                }else{
                    success.invoke(it.data)
                }

            }
            is NetworkResult.Error<T>->{

                if (error == null){
                    content.invoke()
                }else{
                    error.invoke(it.error)
                }

            }
            is NetworkResult.Loading<T>->{
                if (loading == null){
                    content.invoke()
                }else{
                    loading.invoke()
                }

            }
            is NetworkResult.UnSend<T>->{
                if (unSend == null){
                    content.invoke()
                }else{
                    unSend.invoke()
                }

            }
        }
    }
}



suspend fun <T> NetworkResult<T>.getNetwork(
    success : Boolean = true,
    error  : Boolean = true,
    loading : Boolean = false,
    unSend : Boolean = true,
    block:suspend ()->Unit
){
    when(this){
        is NetworkResult.Success<T>->{
            if (success){
                block.invoke()
            }
        }
        is NetworkResult.Error<T>->{
            if (error){
                block.invoke()
            }
        }
        is NetworkResult.Loading<T>->{
            if (loading){
                block.invoke()
            }
        }
        is NetworkResult.UnSend<T>->{
            if (unSend){
                block.invoke()
            }
        }
    }
}

fun  <T>Flow<NetworkResult<T>>.catchWithBinding(
    state : MutableStateFlow<NetworkResult<T>>,
    action: suspend FlowCollector<NetworkResult<T>>.(Throwable) -> Unit
): Flow<NetworkResult<T>> {
    return this.catch {
        println(it.message)
        state.value = NetworkResult.Error(it)
        action.invoke(this,it)
    }
}

fun <T>T.logicWithNullCheck(
    isNull:()->Unit = {},
    isNotNull:(T)->Unit = {},
){
    if(this == null){
        isNull.invoke()
    }
    else{
        isNotNull.invoke(this)
    }
}

@Composable
fun <T>T.logicWithNullCheckInCompose(
    isNull:@Composable ()->Unit = {},
    isNotNull:@Composable (T)->Unit = {},
){
    if(this == null){
        isNull.invoke()
    }
    else{
        isNotNull.invoke(this)
    }
}

fun <T> MutableStateFlow<NetworkResult<T>>.reset(newValue : NetworkResult<T>){
    val oldKey = this.value.key.value
    val newKey = Random(0).nextInt(0,(oldKey+10))
    this.value = newValue.apply {
        key.value = newKey
    }
}

fun <T> MutableStateFlow<NetworkResult<T>>.loading(){
    this.reset(NetworkResult.Loading())
}
fun <T> MutableStateFlow<NetworkResult<T>>.unSend(){
    this.reset(NetworkResult.UnSend())
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.loginIfNotLoading(
    block: suspend () -> Unit
){
    if(this.value !is NetworkResult.Loading){
        this.reset(NetworkResult.Loading())
        block.invoke()
    }
}

fun <T>NetworkResult<T>.logicWithType(
    success: ((T) -> Unit)? = null,
    error: ((Throwable) -> Unit)? = null,
    unSend : (() -> Unit)? = null,
    loading : (() -> Unit)? = null,
){
    when(this){
        is NetworkResult.Success<T> -> {
            success?.invoke(this.data)
        }
        is NetworkResult.Error<T> -> {
            error?.invoke(this.error)
        }
        is NetworkResult.Loading<T> -> {
            loading?.invoke()
        }
        is NetworkResult.UnSend<T> -> {
            unSend?.invoke()
        }
    }
}

fun HttpRequestBuilder.token(token:String){
    this.header("Authorization",token)
}