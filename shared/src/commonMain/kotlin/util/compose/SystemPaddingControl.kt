package util.compose

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.serialization.Serializable

/**
 * 上层ui通知下层ui是否要处理padding，理论上要给予下层ui越大越好的权限
 */
fun Modifier.parentStatusControl(
    boolean: Boolean
) = if (boolean) this else this.statusBarsPadding()

fun Modifier.parentStatusControl(
    parentPaddingControl: ParentPaddingControl
) = if (parentPaddingControl.parentStatusControl) this else this.statusBarsPadding()



fun Modifier.parentNavigationControl(
    boolean: Boolean
) = if (boolean) this else this.navigationBarsPadding()


inline fun Modifier.parentSystemControl(
    parentPaddingControl: ParentPaddingControl
) = this.composed {
    var modifier = this
     this.run {
        if(!parentPaddingControl.parentStatusControl){
            modifier = modifier.statusBarsPadding()
        }
        if(!parentPaddingControl.parentNavigatorControl){
            modifier = modifier.navigationBarsPadding()
        }
    }
    return@composed modifier
}

@Serializable
data class ParentPaddingControl(
    val parentStatusControl:Boolean = false,
    val parentNavigatorControl: Boolean = false
){
    fun copyNew(newParentStatusControl:Boolean = parentStatusControl,newParentNavigatorControl:Boolean = parentNavigatorControl): ParentPaddingControl {
        return ParentPaddingControl(newParentStatusControl,newParentNavigatorControl)
    }
}

fun defaultSelfPaddingControl() = ParentPaddingControl()

fun navigateSelfPaddingControl() = ParentPaddingControl(false,true)

fun statusSelfPaddingControl() = ParentPaddingControl(true,false)

fun allSelfPaddingControl() = ParentPaddingControl(true,true)

