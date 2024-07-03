package dao

import com.liftric.kvault.KVault
import di.globalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import util.kValue.KValueIntData
import util.kValue.KValueStringData

const val StudentTypeKey = "LoginType"
const val SchoolUserNameKey = "SchoolUserName"
const val SchoolPasswordKey = "SchoolPassword"
const val CurrentXnKey = "CurrentXn"
const val CurrentXqKey = "CurrentXq"
const val CurrentWeekKey = "CurrentWeek"
const val UserSchoolIdKey = "UserSchoolId"
const val DataStartDayKey = "DataStartDay"
const val DataStartMonthKey = "DataStartMonth"
const val DataStartYearKey = "DataStartYear"

const val TYPE_UNDERGRADUATE = 0 // 本科生
const val TYPE_POSTGRADUATE = 1 // 研究生
const val TYPE_VISITOR = 2 // 游客

/**
 *
 * @property kValue KVault 对底层操作对象的获取
 * @property schoolUserName KValueStringData 对教务处用户名的操作
 * @property schoolPassword KValueStringData 对教务处密码的操作
 * @property currentXn KValueIntData 对目前学年的操作
 * @property currentXq KValueIntData 对目前学期的操作
 * @property currentWeek KValueIntData 对目前周数的操作
 * @property dataStartDay KValueIntData 对开学日的操作
 * @property dataStartMonth KValueIntData 对开始月的操作
 * @property dataStartYear KValueIntData 对开始年的操作
 * @property currentYear StateFlow<String?> 根据对currentXq和currentXn的计算获取当前学年 如202301
 */
class UndergraduateKValueAction(
    private val kValue: KVault
) {

    val loginType = KValueIntData(StudentTypeKey, MutableStateFlow(null), kValue)
    val schoolUserName = KValueStringData(SchoolUserNameKey, MutableStateFlow(null), kValue)
    val schoolPassword = KValueStringData(SchoolPasswordKey, MutableStateFlow(null), kValue)
    val currentXn = KValueIntData(CurrentXnKey, MutableStateFlow(null), kValue)
    val currentXq = KValueIntData(CurrentXqKey, MutableStateFlow(null), kValue)
    val currentWeek = KValueIntData(CurrentWeekKey, MutableStateFlow(null), kValue)
    val dataStartDay = KValueIntData(DataStartDayKey, MutableStateFlow(null), kValue)
    val dataStartMonth = KValueIntData(DataStartMonthKey, MutableStateFlow(null), kValue)
    val dataStartYear = KValueIntData(DataStartYearKey, MutableStateFlow(null), kValue)

    val currentYear = currentXq.currentValue
        .combine(currentXn.currentValue) { curXueqi, curXuenian ->
            if (curXueqi == null || curXuenian == null) {
                null
            } else "${curXueqi}0$curXuenian"
        }
        .stateIn(
            globalScope,
            SharingStarted.Eagerly,
            "202302"
        )
}