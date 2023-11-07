package ui.util.network

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable

interface NetworkResult<T> {
    data class Success<T>(
        val data:T
    ) : NetworkResult<T>

    data class Error<T>(
        val error: Throwable
    ):NetworkResult<T>

    class Loading<T>:NetworkResult<T>

}

@Composable
fun <T>NetworkResult<T>.ContentByNetwork(
    success : (T)->Unit = {},
    error  : (Throwable)->Unit = {},
    loading : ()->Unit = {},
){
    Crossfade(this){
        when(it){
            is NetworkResult.Success<T>->{
                success.invoke(it.data)
            }
            is NetworkResult.Error<T>->{
                error.invoke(it.error)
            }
            is NetworkResult.Loading<T>->{
                loading.invoke()
            }
        }
    }
}