package dao

import com.liftric.kvault.KVault
import di.globalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import util.kValue.KValueIntDate
import util.kValue.KValueStringDate

const val SchoolUserNameKey = "92AEF5095C772825C850D10E90A14C83"
const val SchoolPasswordKey = "3B89515ED1D172F821C5F96DB93B68D4"
const val CurrentXnKey = "98732304A26DE50AA3AB0482E1EDE29D"
const val CurrentXqKey = "0FB02FA9044F857DDE2698969CDBD922"
const val CurrentWeekKey = "3A71102348BA4E0FCB7FF023B620FF41"
const val UserSchoolIdKey = "EEA8B39A11F52C4D3008558D5A2A1FD4"
const val DataStartDayKey = "80565831CC70729C3E63CE4519801927"
const val DataStartMonthKey = "B3D7460CEE2F189E12B9CE307FE78E6A"
const val DataStartYearKey = "EAFA832A834A4A09612AF4D1E80BCCBF"

class UndergraduateKValueAction(
    private val kValue:KVault
) {

    val schoolUserName = KValueStringDate(SchoolUserNameKey,MutableStateFlow(null),kValue)
    val schoolPassword = KValueStringDate(SchoolPasswordKey,MutableStateFlow(null),kValue)
    val currentXn = KValueIntDate(CurrentXnKey,MutableStateFlow(null),kValue)
    val currentXq = KValueIntDate(CurrentXqKey,MutableStateFlow(null),kValue)
    val currentWeek = KValueIntDate(CurrentWeekKey,MutableStateFlow(null),kValue)
    private val userSchoolId = KValueStringDate(UserSchoolIdKey,MutableStateFlow(null),kValue)
    val dataStartDay = KValueIntDate(DataStartDayKey,MutableStateFlow(null),kValue)
    val dataStartMonth = KValueIntDate(DataStartMonthKey,MutableStateFlow(null),kValue)
    val dataStartYear = KValueIntDate(DataStartYearKey,MutableStateFlow(null),kValue)

    val currentYear = currentXq.currentValue
        .combine(currentXn.currentValue){ curXueqi,curXuenian ->
            if(curXueqi == null || curXuenian == null){
                null
            }
            else "${curXueqi}0$curXuenian"
        }
        .stateIn(
            globalScope,
            SharingStarted.Eagerly,
            "202302"
        )




}