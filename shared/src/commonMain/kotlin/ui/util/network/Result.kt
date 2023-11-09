package ui.util.network

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch

interface NetworkResult<T> {
    data class Success<T>(
        val data:T
    ) : NetworkResult<T>

    data class Error<T>(
        val error: Throwable
    ):NetworkResult<T>

    class Loading<T>:NetworkResult<T>

    class UnSend<T> : NetworkResult<T>
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