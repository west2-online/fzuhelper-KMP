package ui.compose.ClassSchedule

import androidx.compose.foundation.ScrollState
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fleeksoft.ksoup.Ksoup
import com.futalk.kmm.CourseBean
import dao.Dao
import dao.KValueAction
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import di.ClassSchedule
import di.ShareClient
import di.database
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import repository.ClassScheduleRepository
import repository.WeekData
import util.flow.actionWithLabel
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkError
import util.network.resetWithLog
import util.network.resetWithoutLog

/*
    Create by NOSAE on 2024/5/12
    初始加载 当前学期的 开始年 开始月 开始日
*/
@OptIn(ExperimentalCoroutinesApi::class)
class ClassScheduleViewModel (
    private val kValueAction: KValueAction,
    private val classScheduleRepository: ClassScheduleRepository,
    private val classSchedule: ClassSchedule,
    private val dao: Dao,
    private val shareClient: ShareClient
):ViewModel(){
    class ClassScheduleUiState(
        kValueAction: KValueAction,
    ) {
        val startYear = MutableStateFlow(kValueAction.dataStartYear.currentValue.value ?: 2023)
        val startMonth = MutableStateFlow(kValueAction.dataStartMonth.currentValue.value ?: 1)
        val startDay = MutableStateFlow(kValueAction.dataStartDay.currentValue.value ?: 1)
    }

    val classScheduleUiState = ClassScheduleUiState(kValueAction)

    var selectYear = MutableStateFlow(kValueAction.getCurrentYear())
    var selectWeek = MutableStateFlow<Int>(kValueAction.currentWeek.currentValue.value ?: 1)

    val scrollState = ScrollState(initial = 0)
    val courseDialog = MutableStateFlow<CourseBean?>(null)
    val refreshState = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())

    private var course : Flow<List<CourseBean>> = database.classScheduleQueries
        .getAllCourse()
        .asFlow()
        .mapToList(Dispatchers.IO)


    val courseForShow = selectYear
        .combine(course){ currentYear,course ->
            course.filter {
                "${it.kcXuenian}0${it.kcYear}" == currentYear
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            listOf()
        )


    val yearOptions = database.yearOptionsQueries
        .getAllYearOptions()
        .asFlow()
        .mapToList(Dispatchers.IO)


    init {
        viewModelScope.launchInDefault(Dispatchers.Unconfined) {
            classScheduleRepository.apply {
                shareClient.client.apply {
                    getWeek()
                        .map {
                            it.apply {
                                kValueAction.currentWeek.setValue(nowWeek)
                                kValueAction.currentXn.setValue(curXuenian)
                                kValueAction.currentXq.setValue(curXueqi)
                                selectYear.value = getXueQi()
                                selectWeek.value = nowWeek
                            }
                        }
                        .flatMapConcat {
                            getSchoolCalendar(it.getXueQi())
                                .retry(10)
                                .map { result->
                                    parseBeginDateReset(
                                        it.getXueQi()
                                        ,result,
                                        true
                                    )
                                }
                        }
                        .catchWithMassage(
                            label = "更新当前学期",
                            action = null
                        )
                        .collect{}
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    fun refreshClassData(){
        viewModelScope.launchInDefault(Dispatchers.IO) {
            refreshState.logicIfNotLoading {
                classScheduleRepository.apply {
                    val client = classSchedule.getClassScheduleClient() ?: run {
                        refreshState.resetWithLog(
                            logLabel = "登录失败",
                            NetworkResult.Error(Throwable("登录失败"),Throwable("登录失败"))
                        )
                        return@logicIfNotLoading
                    }
                    with(client) {
                        getWeek()
                            .map {
                                it.apply {
                                    kValueAction.currentWeek.setValue(nowWeek)
                                    kValueAction.currentXn.setValue(curXuenian)
                                    kValueAction.currentXq.setValue(curXueqi)
                                    selectYear.value = getXueQi()
                                    selectWeek.value = nowWeek
                                }
                            }
                            .catchWithMassage(
                                label = "更新当前学期失败",
                                action = { label , error ->
                                    refreshState.resetWithLog(
                                        logLabel = "更新当前学期失败",
                                        NetworkResult.Error(Throwable("更新当前学期失败"),error)
                                    )
                                }
                            )
                            .flatMapConcat {
                                getSchoolCalendar(it.getXueQi())
                                    .retry(10)
                                    .map { result->
                                        parseBeginDateReset(it.getXueQi(),result)
                                    }
                            }
                            .actionWithLabel(
                                label = "更新开学日期失败",
                                catchAction = { label , error ->
                                    refreshState.resetWithLog(label,networkError(error,"更新开学日期失败"))
                                },
                                collectAction = { label , data ->
                                    getCourseFromNetwork()
                                }
                            )
                    }
                }
            }
        }
    }

    //获取课程
    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun HttpClient.getCourseFromNetwork(){
        with(classScheduleRepository){
            val id = kValueAction.userSchoolId.currentValue.value
            id ?: run{
                refreshState.resetWithLog(
                    logLabel = "id有误",
                    NetworkResult.Error(Throwable("获取课程失败"),Throwable("id有误"))
                )
                return@with
            }
            with(this@getCourseFromNetwork) {
                getCourseStateHTML(id)
                    .catchWithMassage { label, throwable ->
                        refreshState.resetWithLog(label,networkError(throwable,"更新失败"))
                    }
                    .flatMapConcat { stateHTML ->
                        getWeek()
                            .map { weekDataOnFlow ->
                                CourseData(stateHTML = stateHTML, weekData = weekDataOnFlow)
                            }
                    }
                    .catchWithMassage { label, throwable ->
                        refreshState.resetWithLog(label,networkError(throwable,"更新失败"))
                    }
                    .collect { courseData ->
                        val weekData = courseData.weekData
                        val currentXq = "${weekData.curXueqi}0${weekData.curXuenian}"
                        getCourses( currentXq,courseData.stateHTML)
                            .flatMapConcat {
                                getCoursesHTML(
                                    it,
                                    currentXq,
                                    onGetOptions = { yearOptionsFromNetwork ->
                                        dao.yearOpensDao.insertYearOpens(yearOptionsFromNetwork)
                                    },
                                    id
                                )
                            }
                            .collect { initCourseBean ->
                                dao.classScheduleDao.insertClassScheduleByXueNian(initCourseBean,weekData.curXueqi,weekData.curXuenian)
                                refreshState.resetWithoutLog(NetworkResult.Success("刷新成功"))
                                getOtherCourseFromNetwork(currentXq)
                            }
                    }
            }
        }
    }

    //获取非当前学期的课程
    @OptIn(ExperimentalCoroutinesApi::class)
    fun HttpClient.getOtherCourseFromNetwork(
        currentXq : String,
    ){
        viewModelScope.launchInDefault(Dispatchers.IO) {
            val yearOptions = dao.yearOpensDao.getAllYearOpens()
            yearOptions.filter {
                it.yearOptionsName != currentXq && it.yearOptionsName!=""
            }.map{
                it.yearOptionsName
            }.forEach { yearOptionsName ->
                yearOptionsName.let { xq ->
                    with(classScheduleRepository){
                        val id = kValueAction.userSchoolId.currentValue.value
                        id ?: run{
                            refreshState.resetWithLog(
                                logLabel = "id有误",
                                NetworkResult.Error(Throwable("获取课程失败"),Throwable("id有误"))
                            )
                            return@with
                        }
                        this@getOtherCourseFromNetwork.getCourseStateHTML(
                            id
                        )
                            .flatMapConcat { stateHtml ->
                                getCourses(xq,stateHtml)
                            }

                            .flatMapConcat {
                                this@getOtherCourseFromNetwork.getCoursesHTML(
                                    it,
                                    xq,
                                    onGetOptions = {},
                                    id
                                )
                            }
                            .collect { initCourseBean ->
                                dao.classScheduleDao.insertClassScheduleByXueNian(initCourseBean,xq.substring(0,4).toInt(),xq.substring(5,6).toInt())
                            }
                    }
                }
            }
        }
    }


    //获取考试
    private fun HttpClient.getExamData(){
        viewModelScope.launchInDefault(Dispatchers.IO) {
            val xq = kValueAction.currentXq.currentValue.value
            with(classScheduleRepository) {
                val id = kValueAction.userSchoolId.currentValue.value
                id ?: run{
                    refreshState.resetWithLog(
                        logLabel = "id有误",
                        NetworkResult.Error(Throwable("获取课程失败"),Throwable("id有误"))
                    )
                    return@with
                }
                this@getExamData.getExamStateHTML(id)
                    .map {
                        return@map(parseExamsHTML(it))
                    }
                    .collect {
                        dao.examDao.insertExam(it)
                    }
            }
        }
    }

    fun changeCurrentYear(newValue:String){
        viewModelScope.launchInDefault {
            selectYear.emit(newValue)
            classScheduleRepository.apply {
                 val client = classSchedule.getClassScheduleClient() ?: run {
                     return@apply
                 }
                client.getSchoolCalendar(newValue)
                     .retry(10)
                     .map { result->
                         return@map parseBeginDateReset(newValue,result)
                     }
                     .collectWithMassage { label, data ->

                     }
            }
        }
    }

    private fun parseExamsHTML(result: String): List<ExamBean> {
        val exams = ArrayList<ExamBean>()
        val document = Ksoup.parse(result)
        val examElements = document.select("table[id=ContentPlaceHolder1_DataList_xxk]")
            .select("tr[style=height:30px; border-bottom:1px solid gray; border-left:1px solid gray; vertical-align:middle;]")
        println("getExamInfo: examList:" + examElements.size)
        for (i in examElements.indices) {
            val element = examElements[i]
            val tds = element.select("td")
            val name = tds[0].text()
            val xuefen = tds[1].text()
            val teacher = tds[2].text()
            val address = tds[3].text()
            val zuohao = tds[4].text()
            if (address.isNotEmpty()) {

            }
            val exam = ExamBean(name, xuefen, teacher, address, zuohao)
            exams.add(exam)
        }
        println(exams)
        return exams
    }

    override fun onCleared() {
        super.onCleared()
        println("over_v")
    }

    data class CourseData(
        val stateHTML:String,
        val weekData: WeekData
    )
    /**
     * 解析开学时间网页
     * @param result 获取到的网页
     */
    private suspend fun parseBeginDateReset(
        xq: String,
        result: String,
        save : Boolean = false
    ) {
        val document = Ksoup.parse(result)
        val select = document.getElementsByTag("select")[0]
        val option = select.getElementsByAttributeValueStarting("value", xq)
        if (option.size > 0) {
            val value = option[0].attr("value")
            val beginYear = value.substring(6, 10).toInt()
            val beginMonth = value.substring(10, 12).toInt()
            val beginDay = value.substring(12, 14).toInt()
            if (save){
                kValueAction.apply {
                    dataStartDay.setValue(beginDay)
                    dataStartMonth.setValue(beginMonth)
                    dataStartYear.setValue(beginYear)
                }
            }
            classScheduleUiState.startDay.emit(beginDay)
            classScheduleUiState.startYear.emit(beginYear)
            classScheduleUiState.startMonth.emit(beginMonth)
//            println(beginMonth)
//                val calendar = Calendar.getInstance()
//                calendar.set(beginYear, beginMonth - 1, beginDay, 0, 0, 0)
            //存储当前设置的学期开学时间
//                DataManager.beginDate = calendar.timeInMillis
            //根据开学时间和当前时间计算出当前周数
//                val startMillis = calendar.timeInMillis
//                val endYear = value.substring(14, 18).toInt()
//                val endMonth = value.substring(18, 20).toInt()
//                val endDay = value.substring(20, 22).toInt()
//                calendar.set(endYear, endMonth - 1, endDay)
//                val endMillis = calendar.timeInMillis
//                val endWeek = getWeeks(startMillis, endMillis)
//                DataManager.endWeek = endWeek
        }


    }
}

data class ExamBean(
    var name: String = "",
    var xuefen: String = "",
    var teacher: String = "",
    var address: String = "",
    var zuohao: String = ""
)
