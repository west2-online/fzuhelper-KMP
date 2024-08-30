package util.compose

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import kotlinx.serialization.Serializable

/**
 * 根据boolean使用statusBarsPadding()
 *
 * @param boolean Boolean
 * @return Modifier
 * @receiver Modifier
 */
fun Modifier.parentStatusControl(boolean: Boolean) = if (boolean) this else this.statusBarsPadding()

/**
 * 根据parentPaddingControl使用statusBarsPadding()
 *
 * @param parentPaddingControl ParentPaddingControl
 * @return Modifier
 * @receiver Modifier
 */
fun Modifier.parentStatusControl(parentPaddingControl: ParentPaddingControl) =
  if (parentPaddingControl.parentStatusControl) this else this.statusBarsPadding()

/**
 * 根据 boolean参数使用 navigationBarsPadding()
 *
 * @param boolean Boolean
 * @return Modifier
 * @receiver Modifier
 */
fun Modifier.parentNavigationControl(boolean: Boolean) =
  if (boolean) this else this.navigationBarsPadding()

/**
 * 根据 parentPaddingControl参数来使用statusBarsPadding()和navigationBarsPadding()
 *
 * @param parentPaddingControl ParentPaddingControl
 * @return Modifier
 * @receiver Modifier
 */
inline fun Modifier.parentSystemControl(parentPaddingControl: ParentPaddingControl) =
  this.composed {
    var modifier = this
    this.run {
      if (!parentPaddingControl.parentStatusControl) {
        modifier = modifier.statusBarsPadding()
      }
      if (!parentPaddingControl.parentNavigatorControl) {
        modifier = modifier.navigationBarsPadding()
      }
    }
    return@composed modifier
  }

/**
 * 上层ui对下层ui的沉浸式通知，只是通知，不是强制
 *
 * @property parentStatusControl Boolean 上层是否已经处理了状态栏
 * @property parentNavigatorControl Boolean 上层是否已经处理了底部栏
 * @constructor
 */
@Serializable
data class ParentPaddingControl(
  val parentStatusControl: Boolean = false,
  val parentNavigatorControl: Boolean = false,
) {
  fun copyNew(
    newParentStatusControl: Boolean = parentStatusControl,
    newParentNavigatorControl: Boolean = parentNavigatorControl,
  ): ParentPaddingControl {
    return ParentPaddingControl(newParentStatusControl, newParentNavigatorControl)
  }
}

/**
 * 让 ui自行处理沉浸式
 *
 * @return ParentPaddingControl
 */
fun defaultSelfPaddingControl() = ParentPaddingControl()

/**
 * ui自己处理状态栏，无需关心底部栏
 *
 * @return ParentPaddingControl
 */
fun navigateSelfPaddingControl() = ParentPaddingControl(false, true)

/**
 * ui自己处理底部栏，无需关心状态栏
 *
 * @return ParentPaddingControl
 */
fun statusSelfPaddingControl() = ParentPaddingControl(true, false)

/**
 * ui无需关心沉浸式
 *
 * @return ParentPaddingControl
 */
fun allSelfPaddingControl() = ParentPaddingControl(true, true)
