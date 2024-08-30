package dao

import com.liftric.kvault.KVault
import kotlinx.coroutines.flow.MutableStateFlow
import util.kValue.KValueStringData

const val FontTokenKey = "Font"
const val ThemeTokenKey = "Theme"
const val TransitionTokenKey = "Transition"

/**
 * 有关主题的键值对封装
 *
 * @property kValue KVault 获取底层的操作对象
 * @property fontToken KValueStringData 对字体的操作
 * @property themeToken KValueStringData 对主题的操作
 * @property transitionToken KValueStringData 对切屏动画的操作
 * @constructor
 */
class ThemeKValueAction(private val kValue: KVault) {
  val fontToken = KValueStringData(FontTokenKey, MutableStateFlow(null), kValue)
  val themeToken = KValueStringData(ThemeTokenKey, MutableStateFlow(null), kValue)
  val transitionToken = KValueStringData(TransitionTokenKey, MutableStateFlow(null), kValue)
}
