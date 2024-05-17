package dao

import com.liftric.kvault.KVault
import kotlinx.coroutines.flow.MutableStateFlow
import util.kValue.KValueStringDate


const val FontTokenKey = "Font"
const val ThemeTokenKey = "Theme"
const val TransitionTokenKey = "Transition"

/**
 * 有关主题的键值对封装
 * @property kValue KVault 获取底层的操作对象
 * @property fontToken KValueStringDate 对字体的操作
 * @property themeToken KValueStringDate 对主题的操作
 * @property transitionToken KValueStringDate 对切屏动画的操作
 * @constructor
 */
class ThemeKValueAction(
    private val kValue: KVault
) {
    val fontToken = KValueStringDate(FontTokenKey, MutableStateFlow(null),kValue)
    val themeToken = KValueStringDate(ThemeTokenKey, MutableStateFlow(null),kValue)
    val transitionToken = KValueStringDate(TransitionTokenKey, MutableStateFlow(null),kValue)
}