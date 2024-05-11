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
import di.CookieUtil
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
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.launch
import repository.ClassScheduleRepository
import repository.WeekData
import util.flow.collectWithMassage
import util.flow.launchInDefault

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
        val startYear = kValueAction.dataStartYear.currentValue.map {
            return@map it?:2023
        }
        val startMonth = kValueAction.dataStartMonth.currentValue.map {
            return@map it?:1
        }
        val startDay = kValueAction.dataStartDay.currentValue.map {
            return@map it?:1
        }
    }
    val classScheduleUiState = ClassScheduleUiState(kValueAction)

    init {
        viewModelScope.launchInDefault {
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
                                    parseBeginDateReset(it.getXueQi(),result)
                                }
                        }.collect{}
                }
            }
        }
    }

    var selectYear = MutableStateFlow(kValueAction.getCurrentYear())
    var selectWeek = MutableStateFlow<Int>(kValueAction.currentWeek.currentValue.value ?: 1)

    val scrollState = ScrollState(initial = 0)
    val academicYearSelectsDialogState = MutableStateFlow(false)
    val courseDialog = MutableStateFlow<CourseBean?>(null)


    private var course : Flow<List<CourseBean>> = database.classScheduleQueries
        .getAllCourse()
        .asFlow()
        .mapToList(Dispatchers.IO)


    val courseForShow = selectYear
        .combine(course){
            currentYear,course -> course.filter {
                "${it.kcYear}0${it.kcXuenian}" == currentYear
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


    @OptIn(ExperimentalCoroutinesApi::class)
    fun refreshInitData(){
        viewModelScope.launch(Dispatchers.IO) {
            classScheduleRepository.apply {
                val client =  classSchedule.getClassScheduleClient()
                client.getWeek()
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
                        client.getSchoolCalendar(it.getXueQi())
                            .retry(10)
                            .map { result->
                                parseBeginDateReset(it.getXueQi(),result)
                            }
                    }.collect {
                        client.getCourseFromNetwork()
                        client.getExamData()
                    }
            }
        }
    }

    //获取课程
    @OptIn(ExperimentalCoroutinesApi::class)
    fun HttpClient.getCourseFromNetwork(){
        viewModelScope.launchInDefault {
            classScheduleRepository.apply{
                val id = kValueAction.userSchoolId.currentValue.value
                id ?: run{
                    return@launchInDefault
                }
                this@getCourseFromNetwork.getCourseStateHTML(id)
                    .zip(
                        this@getCourseFromNetwork.getWeek()
                    ){ stateHTML, weekDataOnFlow ->
                        CourseData(stateHTML = stateHTML, weekData = weekDataOnFlow)
                    }
                    .collect { courseData ->
                        val weekData = courseData.weekData
//                        setDataManageDataStore(DataManagePreferencesKey.DATA_MANAGE_CURRENT_WEEK,weekData.nowWeek.toString())
                        this@getCourseFromNetwork.getCourses("${weekData.curXueqi}0${weekData.curXuenian}",courseData.stateHTML)
                            .flatMapConcat {
                                this@getCourseFromNetwork.getCoursesHTML(
                                    it,
                                    "${weekData.curXueqi}0${weekData.curXuenian}",
                                    onGetOptions = { yearOptionsFromNetwork ->
                                        dao.yearOpensDao.insertYearOpens(yearOptionsFromNetwork)
                                    }
                                )
                            }
                            .collect { initCourseBean ->
                                dao.classScheduleDao.insertClassSchedule(initCourseBean)
                                this@getCourseFromNetwork.getOtherCourseFromNetwork("${weekData.curXueqi}0${weekData.curXuenian}")
                            }
                    }
            }
        }
    }

    //获取非当前学期的课程
    @OptIn(ExperimentalCoroutinesApi::class)
    fun HttpClient.getOtherCourseFromNetwork(
        loadedYear : String,
    ){
        viewModelScope.launch(Dispatchers.IO) {
            val yearOptions = dao.yearOpensDao.getAllYearOpens()
            yearOptions.filter {
                it.yearOptionsName != loadedYear && it.yearOptionsName!=""
            }.map{
                it.yearOptionsName
            }.forEach { yearOptionsName ->
                yearOptionsName.let { xq ->
                    classScheduleRepository.apply{
                        this@getOtherCourseFromNetwork.getCourseStateHTML(
                            CookieUtil.id
                        )
                            .flatMapConcat { stateHtml ->
                                getCourses(xq,stateHtml)
                            }

                            .flatMapConcat {
                                this@getOtherCourseFromNetwork.getCoursesHTML(
                                    it,
                                    xq,
                                    onGetOptions = {}
                                )
                            }
                            .collect { initCourseBean ->
                                dao.classScheduleDao.deleteClassScheduleByXq(xq.substring(0,4),xq.substring(5,6))
                                dao.classScheduleDao.insertClassSchedule(initCourseBean)
                            }
                    }
                }
            }
        }
    }

    //获取考试
    private fun HttpClient.getExamData(){
        viewModelScope.launch(Dispatchers.IO) {
            val xq = kValueAction.currentXq.currentValue.value
            classScheduleRepository.apply {
                this@getExamData.getExamStateHTML()
                    .map {
                        return@map(parseExamsHTML(it))
                    }
                    .collect {
                        dao.examDao.insertExam(it)
                    }
            }
        }
    }

    fun refreshCourse(){
        viewModelScope.launch(Dispatchers.IO) {
            val client = classSchedule.getClassScheduleClient()
            client.getCourseFromNetwork()
        }
    }

    fun changeCurrentYear(newValue:String){
        viewModelScope.launch {
            selectYear.emit(newValue)
            classScheduleRepository.apply {
                 classSchedule.getClassScheduleClient().getSchoolCalendar(newValue)
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
    private suspend fun parseBeginDateReset(xq: String, result: String) {
        val document = Ksoup.parse(result)
        val select = document.getElementsByTag("select")[0]
        val option = select.getElementsByAttributeValueStarting("value", xq)
        if (option.size > 0) {
            val value = option[0].attr("value")
            val beginYear = value.substring(6, 10).toInt()
            val beginMonth = value.substring(10, 12).toInt()
            val beginDay = value.substring(12, 14).toInt()
            kValueAction.apply {
                dataStartDay.setValue(beginDay)
                dataStartMonth.setValue(beginMonth)
                dataStartYear.setValue(beginYear)
            }
            println(beginMonth)
            println(beginYear)
            println(beginDay)
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
