package dao

import com.liftric.kvault.KVault
import kotlinx.coroutines.flow.MutableStateFlow
import util.kValue.KValueStringData

const val NetworkKey = "token"
class TokenKValueAction(
    val kVault: KVault
) {
    val token = KValueStringData(key = NetworkKey, data =MutableStateFlow(null), kValue = kVault)
}