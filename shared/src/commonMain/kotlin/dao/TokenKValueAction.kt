package dao

import com.liftric.kvault.KVault
import kotlinx.coroutines.flow.MutableStateFlow
import util.kValue.KValueStringDate

const val NetworkKey = "token"
class TokenKValueAction(
    val kVault: KVault
) {
    val token = KValueStringDate(key = NetworkKey, data =MutableStateFlow(null), kValue = kVault)
}