package dao

import com.liftric.kvault.KVault

const val SchoolUserNameKey = "92AEF5095C772825C850D10E90A14C83"
const val SchoolPasswordKey = "3B89515ED1D172F821C5F96DB93B68D4"
const val CurrentXnKey = "98732304A26DE50AA3AB0482E1EDE29D"
const val CurrentXqKey = "0FB02FA9044F857DDE2698969CDBD922"
const val CurrentWeekKey = "3A71102348BA4E0FCB7FF023B620FF41"
const val UserSchoolIdKey = "EEA8B39A11F52C4D3008558D5A2A1FD4"
const val DataStartDayKey = ""
const val DataStartMonthKey = ""
const val DataStartYearKey = ""
class KValueAction(
    private val kValue:KVault
) {
    fun getUserName(): String? {
        return kValue.string(SchoolUserNameKey)
    }
    fun setUserName(userName : String): Boolean {
        return kValue.set(SchoolUserNameKey,userName)
    }

    fun getSchoolPassword(): String? {
        return kValue.string(SchoolPasswordKey)
    }
    fun setSchoolPassword(password:String): Boolean {
        return kValue.set(SchoolPasswordKey,password)
    }

    fun setCurrentXn(currentXn:Int): Boolean {
        return kValue.set(CurrentXnKey,currentXn)
    }
    fun getCurrentXn(): Int? {
        return kValue.int(CurrentXnKey)
    }

    fun setCurrentXq(currentXq:Int): Boolean {
        return kValue.set(CurrentXqKey,currentXq)
    }
    fun getCurrentXq(): Int? {
        return kValue.int(CurrentXqKey)
    }

    fun getCurrentWeek(): String? {
        return kValue.string(CurrentWeekKey)
    }
    fun setCurrentWeek(currentWeek:Int): Boolean {
        return kValue.set(CurrentWeekKey,currentWeek)
    }

    fun getUserSchoolId(): String? {
        return kValue.string(UserSchoolIdKey)
    }
    fun setUserSchoolId(userSchoolId:Int): Boolean {
        return kValue.set(UserSchoolIdKey,userSchoolId)
    }

    fun getDateStartDay(): Int? {
        return kValue.int(DataStartDayKey)
    }
    fun setDateStartDay(startDay : Int): Boolean {
        return kValue.set(DataStartDayKey,startDay)
    }

    fun getDateStartMonth(): Int? {
        return kValue.int(DataStartMonthKey)
    }
    fun setDateStartMonth(startMonth:Int): Boolean {
        return kValue.set(DataStartMonthKey,startMonth)
    }

    fun getDateStartYear(): Int? {
        return kValue.int(DataStartYearKey)
    }
    fun setDateStartYear(startYear:Int): Boolean {
        return kValue.set(DataStartYearKey,startYear)
    }
}