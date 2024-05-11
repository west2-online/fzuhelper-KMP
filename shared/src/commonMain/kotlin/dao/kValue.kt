package dao

import com.liftric.kvault.KVault
import di.globalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import util.flow.launchInIO

const val SchoolUserNameKey = "92AEF5095C772825C850D10E90A14C83"
const val SchoolPasswordKey = "3B89515ED1D172F821C5F96DB93B68D4"
const val CurrentXnKey = "98732304A26DE50AA3AB0482E1EDE29D"
const val CurrentXqKey = "0FB02FA9044F857DDE2698969CDBD922"
const val CurrentWeekKey = "3A71102348BA4E0FCB7FF023B620FF41"
const val UserSchoolIdKey = "EEA8B39A11F52C4D3008558D5A2A1FD4"
const val DataStartDayKey = "80565831CC70729C3E63CE4519801927"
const val DataStartMonthKey = "B3D7460CEE2F189E12B9CE307FE78E6A"
const val DataStartYearKey = "EAFA832A834A4A09612AF4D1E80BCCBF"

class KValueAction(
    private val kValue:KVault
) {

    val schoolUserName = KValueStringDate(SchoolUserNameKey,MutableStateFlow(null))
    val schoolPassword = KValueStringDate(SchoolPasswordKey,MutableStateFlow(null))
    val currentXn = KValueIntDate(CurrentXnKey,MutableStateFlow(null))
    val currentXq = KValueIntDate(CurrentXqKey,MutableStateFlow(null))
    val currentWeek = KValueIntDate(CurrentWeekKey,MutableStateFlow(null))
    val userSchoolId = KValueStringDate(UserSchoolIdKey,MutableStateFlow(null))
    val dataStartDay = KValueIntDate(DataStartDayKey,MutableStateFlow(null))
    val dataStartMonth = KValueIntDate(DataStartMonthKey,MutableStateFlow(null))
    val dataStartYear = KValueIntDate(DataStartYearKey,MutableStateFlow(null))


    fun getCurrentYear():String?{
        val curXueqi = currentXq.currentValue.value
        val curXuenian = currentXn.currentValue.value
        if(curXueqi == null || curXuenian == null){
            return null
        }
        return "${curXueqi}0$curXuenian}"
    }




    inner class KValueStringDate (
        val key: String,
        private val data:MutableStateFlow<String?>
    ){
        val currentValue = data.asStateFlow()
        private suspend fun  store(state:MutableStateFlow<String?>, key:String){
            state.collect{
                state.value?.let { it1 -> kValue.set(key, it1) }
            }
        }
        suspend fun setValue(newValue:String?){
            data.emit(newValue)
        }
        init {
            globalScope.launchInIO {
                data.value = kValue.string(key)
                store(data,key)
            }
        }
    }

    inner class KValueIntDate (
        val key: String,
        private val data:MutableStateFlow<Int?>
    ){
        val currentValue = data.asStateFlow()
        private suspend fun  store(state:MutableStateFlow<Int?>, key:String){
            state.collect{
                state.value?.let { it1 -> kValue.set(key, it1) }
            }
        }
        suspend fun setValue(newValue:Int?){
            data.emit(newValue)
        }
        init {
            globalScope.launchInIO {
                data.value = kValue.int(key)
                store(data,key)
            }
        }
    }
}