package ui.util.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

data class ToastImp(
    val icon : ImageVector? = null,
    val text: String,
    val color: Color = Color.Cyan
)

class Toast(private val scope: CoroutineScope) {
    var currentToast = mutableStateOf<ToastImp>(ToastImp(null,"", Color.Cyan))
        private set
    var isShow = mutableStateOf(false)
        private set
    private val mutex = Mutex()
    suspend fun show(toast : ToastImp):Unit{
        if(!mutex.isLocked){
            mutex.withLock {
                try {
                    currentToast.value = toast
                    isShow.value = true
                }finally {
                    delay(2000)
                    isShow.value = false
                }
            }
        }
    }

    fun run (toast : ToastImp){
        scope.launch {
            show(toast)
        }
    }

    fun addToast(string: String,color: Color = Color.Gray){
        run(ToastImp(Icons.Filled.Refresh,string, color))
    }

    fun addWarnToast(string: String){
        run(ToastImp(Icons.Filled.Refresh,string,Color(248, 102, 95)))
    }

}


@Composable
inline fun rememberToastState(
    scope: CoroutineScope = rememberCoroutineScope()
): Toast = remember{
    Toast(scope)
}





@Composable
fun EasyToast(
    toast : Toast = rememberToastState()
){
    rememberLazyListState()
    AnimatedVisibility(
        toast.isShow.value,
        exit =  slideOutVertically() { 0 } + shrinkVertically() + fadeOut(tween(5000)),
        enter = slideInVertically{0}
    ){
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(10.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(toast.currentToast.value.color)
                .padding(10.dp)
        ){
            toast.currentToast.value.let {
                Text(
                    it.text,
                    modifier = Modifier
                        .align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}