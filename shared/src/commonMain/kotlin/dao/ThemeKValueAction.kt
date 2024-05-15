package dao

import com.liftric.kvault.KVault
import kotlinx.coroutines.flow.MutableStateFlow
import util.kValue.KValueStringDate


const val FontTokenKey = "Font"
const val ThemeTokenKey = "Theme"
const val TransitionTokenKey = "Transition"

class ThemeKValueAction(
    private val kValue: KVault
) {
    val fontToken = KValueStringDate(FontTokenKey, MutableStateFlow(null),kValue)
    val themeToken = KValueStringDate(ThemeTokenKey, MutableStateFlow(null),kValue)
    val transitionToken = KValueStringDate(TransitionTokenKey, MutableStateFlow(null),kValue)
}