package util.network

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import annotation.ImportantFunction
import di.database
import di.globalScope
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import util.flow.launchInDefault
import kotlin.random.Random

@Stable
interface NetworkResult<T> {
    val key: MutableState<Int>
    var showToast :Boolean
    var hasDeal : Boolean
    data class Success<T>(
        val dataForShow:T,
        val rawData:T ? = null,
        override var showToast: Boolean = true,
        override val key: MutableState<Int> = mutableStateOf(0),
        override var hasDeal: Boolean = false,
    ) : NetworkResult<T>

    data class Error<T>(
        val errorForShow: Throwable,
        val rawError:Throwable,
        override var showToast: Boolean = true,
        override val key: MutableState<Int> = mutableStateOf(0),
        override var hasDeal: Boolean = false,
    ): NetworkResult<T>

    class LoadingWithAction<T>(
        override var showToast: Boolean = true,
        override val key: MutableState<Int> = mutableStateOf(0),
        override var hasDeal: Boolean = false,
    ):
        NetworkResult<T>


    class LoadingWithOutAction<T>(override var showToast: Boolean = true, override val key: MutableState<Int> = mutableStateOf(0),
                                  override var hasDeal: Boolean = false,):
        NetworkResult<T>

    class UnSend<T>( override var showToast: Boolean = true,override val key: MutableState<Int> = mutableStateOf(0),
                     override var hasDeal: Boolean = false,) :
        NetworkResult<T>

}

fun <T>networkError(rawError: String,error: String) = NetworkResult.Error<T>(Throwable(rawError),Throwable(error))
fun <T>networkError(rawError: Error,error: String) = NetworkResult.Error<T>(Throwable(rawError),Throwable(error))
fun networkSuccess(success: String) = NetworkResult.Success<String>(success)


fun <T>networkErrorWithLog( errorCode :Int,newDescribe : String ) = NetworkResult.Error<T>(
    rawError = Throwable("Error Code : $errorCode"),
    errorForShow = Throwable(newDescribe)
)

fun <T>networkErrorWithLog( error :Throwable,newDescribe : String ) = NetworkResult.Error<T>(
    rawError = error,
    errorForShow = Throwable(newDescribe)
)

@Composable
fun <T> State<NetworkResult<T>>.CollectWithContent(
    success : (@Composable (T)->Unit)? = null,
    error  : (@Composable (Throwable)->Unit)? = null,
    loading : (@Composable ()->Unit)? = null,
    unSend : (@Composable ()->Unit)? = null,
    content :@Composable ()->Unit = {},
    modifier: Modifier = Modifier
        .fillMaxSize()
){
    Crossfade(
        this.value,
        modifier = modifier
    ){
        when(it){
            is NetworkResult.Success<T> ->{
                if (success == null){
                    content.invoke()
                }else{
                    success.invoke(it.dataForShow)
                }

            }
            is NetworkResult.Error<T> ->{

                if (error == null){
                    content.invoke()
                }else{
                    error.invoke(it.errorForShow)
                }

            }
            is NetworkResult.LoadingWithAction<T> ->{
                if (loading == null){
                    content.invoke()
                }else{
                    loading.invoke()
                }

            }
            is NetworkResult.UnSend<T> ->{
                if (unSend == null){
                    content.invoke()
                }else{
                    unSend.invoke()
                }
            }

            is NetworkResult.LoadingWithOutAction<T> ->{
                if (loading == null){
                    content.invoke()
                }else{
                    loading.invoke()
                }
            }
        }
    }
}


@Composable
fun <T> State<NetworkResult<T>>.CollectWithContentInBox(
    success : (@Composable BoxScope.(T)->Unit)? = null,
    error  : (@Composable BoxScope.(Throwable)->Unit)? = null,
    loading : (@Composable BoxScope.()->Unit)? = null,
    unSend : (@Composable BoxScope.()->Unit)? = null,
    content :@Composable BoxScope.()->Unit = {},
    modifier: Modifier = Modifier
){
    Crossfade(
        this.value,
        modifier = modifier
    ){
        Box(
            modifier = Modifier
                .fillMaxSize()
        ){
            when(it){
                is NetworkResult.Success<T> ->{
                    if (success == null){
                        content.invoke(this)
                    }else{
                        success.invoke(this,it.dataForShow)
                    }

                }
                is NetworkResult.Error<T> ->{

                    if (error == null){
                        content.invoke(this)
                    }else{
                        error.invoke(this,it.errorForShow)
                    }

                }
                is NetworkResult.LoadingWithAction<T> ->{
                    if (loading == null){
                        content.invoke(this)
                    }else{
                        loading.invoke(this)
                    }

                }
                is NetworkResult.UnSend<T> ->{
                    if (unSend == null){
                        content.invoke(this)
                    }else{
                        unSend.invoke(this)
                    }
                }

                is NetworkResult.LoadingWithOutAction<T> ->{
                    if (loading == null){
                        content.invoke(this)
                    }else{
                        loading.invoke(this)
                    }
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
        is NetworkResult.Success<T> ->{
            if (success){
                block.invoke()
            }
        }
        is NetworkResult.Error<T> ->{
            if (error){
                block.invoke()
            }
        }
        is NetworkResult.LoadingWithAction<T> ->{
            if (loading){
                block.invoke()
            }
        }
        is NetworkResult.UnSend<T> ->{
            if (unSend){
                block.invoke()
            }
        }
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

@ImportantFunction
suspend fun <T> MutableStateFlow<NetworkResult<T>>.resetWithLog(logLabel:String, newValue : NetworkResult<T>){
    if(newValue is NetworkResult.Error){
        globalScope.launchInDefault {
            var errorMassage = ""
            while (newValue.rawError.cause != null){
                errorMassage += "${if (errorMassage.isEmpty()) "" else "-->"} @{$logLabel} ${ newValue.rawError.message.toString()}"
            }
            database.networkLogQueries.insertNetworkErrorLog(time = Clock.System.now().toString(),error = errorMassage)
        }
    }
    val oldKey = this.value.key.value
    val newKey = Random(0).nextInt(oldKey+10,(oldKey+100))
    this.value = newValue.apply {
        key.value = newKey
    }
    delay(1500)
    this.value.showToast = false
}

@ImportantFunction
suspend fun <T> MutableStateFlow<NetworkResult<T>>.resetWithoutLog( newValue : NetworkResult<T>){
    val oldKey = this.value.key.value
    val newKey = Random(0).nextInt(oldKey+10,(oldKey+100))
    this.value = newValue.apply {
        key.value = newKey
    }
    delay(1500)
    this.value.showToast = false
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.intoLoading(){
    val oldKey = this.value.key.value
    val newKey = Random(0).nextInt(0,(oldKey+10))
    this.value = NetworkResult.LoadingWithAction<T>().apply {
        key.value = newKey
    }
    delay(1500)
    this.value.showToast = false
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.loading(){
    this.resetWithoutLog(NetworkResult.LoadingWithAction())
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.unSend(){
    this.resetWithoutLog(NetworkResult.UnSend())
}

suspend fun <T> MutableStateFlow<NetworkResult<T>>.logicIfNotLoading(
    preAction: suspend ()->Unit = {},
    block: suspend () -> Unit
){
    preAction.invoke()
    if(this.value !is NetworkResult.LoadingWithAction){
        this.resetWithoutLog(NetworkResult.LoadingWithAction())
        block.invoke()
    }
}
suspend fun <T> MutableStateFlow<NetworkResult<T>>.logicIfUnSendInSuspend(
    preAction: suspend ()->Unit = {},
    block: suspend () -> Unit
){
    preAction.invoke()
    if(this.value is NetworkResult.UnSend){
        block.invoke()
    }
}

fun <T> MutableStateFlow<NetworkResult<T>>.logicIfUnSend(
    block: () -> Unit
){
    if(this.value is NetworkResult.UnSend){
        block.invoke()
    }
}


//对网络结果的处理，可以处理无数次
fun <T> NetworkResult<T>.logicWithTypeWithoutLimit(
    success: ((T) -> Unit)? = null,
    error: ((Throwable) -> Unit)? = null,
    unSend : (() -> Unit)? = null,
    loading : (() -> Unit)? = null,
){
    when(this){
        is NetworkResult.Success<T> -> {
            success?.invoke(this.dataForShow)
        }
        is NetworkResult.Error<T> -> {
            error?.invoke(this.errorForShow)
        }
        is NetworkResult.LoadingWithAction<T> -> {
            loading?.invoke()
        }
        is NetworkResult.UnSend<T> -> {
            unSend?.invoke()
        }
        is NetworkResult.LoadingWithOutAction<T> ->{
            loading?.invoke()
        }
    }
}

//对网络结果的处理，只能处理一次
fun <T> NetworkResult<T>.logicWithTypeWithLimit(
    success: ((T) -> Unit)? = null,
    error: ((Throwable) -> Unit)? = null,
    unSend : (() -> Unit)? = null,
    loading : (() -> Unit)? = null,
){
    if(!this.hasDeal){
        this.hasDeal = true
        when(this){
            is NetworkResult.Success<T> -> {
                success?.invoke(this.dataForShow)
            }
            is NetworkResult.Error<T> -> {
                error?.invoke(this.errorForShow)
            }
            is NetworkResult.LoadingWithAction<T> -> {
                loading?.invoke()
            }
            is NetworkResult.UnSend<T> -> {
                unSend?.invoke()
            }
            is NetworkResult.LoadingWithOutAction<T> ->{
                loading?.invoke()
            }
        }
    }
}

suspend fun <T> NetworkResult<T>.toast(
    success: ((T) -> Unit)? = null,
    error: ((Throwable) -> Unit)? = null,
    unSend : (() -> Unit)? = null,
    loading : (() -> Unit)? = null,
){
    if(this.showToast){
        this.logicWithTypeWithoutLimit(
            success = success,
            error = error,
            unSend = unSend,
            loading = loading,
        )
    }
}


fun HttpRequestBuilder.token(token:String){
    this.header("Authorization",token)
}