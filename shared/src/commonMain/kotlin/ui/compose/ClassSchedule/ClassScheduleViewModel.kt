package ui.compose.ClassSchedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.ui.graphics.ImageBitmap
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fleeksoft.ksoup.Ksoup
import com.futalk.kmm.CourseBean
import dao.Dao
import dao.KValueAction
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import di.ClassSchedule
import di.CookieUtil
import di.database
import io.ktor.client.HttpClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
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
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.flow.launchInDefault
import util.network.NetworkResult

class ClassScheduleViewModel (
    private val kValueAction: KValueAction,
    private val classScheduleRepository: ClassScheduleRepository,
    private val classSchedule: ClassSchedule,
    private val dao: Dao
):ViewModel(){
    class ClassScheduleUiState {
        val selectYear = MutableStateFlow<Int>(0)
        val selectMonth = MutableStateFlow<Int>(0)
        val selectDay = MutableStateFlow<Int>(0)
    }
    val classScheduleUiState = ClassScheduleUiState()
    private var course : Flow<List<CourseBean>> = database.classScheduleQueries
        .getAllCourse()
        .asFlow()
        .mapToList(Dispatchers.IO)

    var currentYear = MutableStateFlow<String?>(null)
    var currentWeek = MutableStateFlow<Int>(1)
    val yearOptions = database.yearOptionsQueries
        .getAllYearOptions()
        .asFlow()
        .mapToList(Dispatchers.IO)

    val scrollState = ScrollState(initial = 0)
    @OptIn(ExperimentalFoundationApi::class)


    val academicYearSelectsDialogState = MutableStateFlow(false)
    val courseDialog = MutableStateFlow<CourseBean?>(null)

    val refreshDialog = MutableStateFlow(false)
    var refreshDialogVerificationCode =  MutableStateFlow<ImageBitmap?>(null)
    val refreshVerificationCodeState  =  MutableStateFlow<NetworkResult<String>>(NetworkResult.LoadingWithAction())
    val refreshClickAble = MutableStateFlow(true)
    val refreshButtonState = MutableStateFlow<NetworkResult<String>>(NetworkResult.LoadingWithAction())
    val courseForShow = currentYear
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

    @OptIn(ExperimentalCoroutinesApi::class)
    fun refreshInitData(){
        viewModelScope.launch(Dispatchers.IO) {
            classScheduleRepository.apply {
                val client = async {
                    classSchedule.getClassScheduleClient()
                }.await()
                client.getWeek()
                    .map {
                        it.apply {
                            kValueAction.setCurrentWeek(nowWeek)
                            kValueAction.setCurrentXn(curXuenian)
                            kValueAction.setCurrentXq(curXueqi)
                            currentYear.value = getXueQi()
                            currentWeek.value = nowWeek
                        }
                    }
                    .catchWithMassage { label, throwable ->
                        TODO()
                    }
                    .flatMapConcat {
                        client.getSchoolCalendar(it.getXueQi())
                            .retry(10)
                            .map { result->
                                parseBeginDate(it.getXueQi(),result)
                            }
                    }.collect {
                        client.getCourseFromNetwork()
                        client.getExamData()
                    }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun HttpClient.getCourseFromNetwork(){
        viewModelScope.launchInDefault {
            classScheduleRepository.apply{
                val id = kValueAction.getUserSchoolId()
                id ?: run{
                    return@launchInDefault
                }
                getExamData()
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
                yearOptionsName?.let { xq ->
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
                                    onGetOptions = {
//                                        yearOptionsFromNetwork ->
//                                        yearOptions.value = yearOptionsFromNetwork
                                    }
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

    private fun HttpClient.getExamData(){
        viewModelScope.launch(Dispatchers.IO) {
            val xq = kValueAction.getCurrentXq()
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
//            else{
//                refreshDialog.value = true
////                BlockLoginPageRepository.getVerifyCode().catch {
////                    refreshVerificationCodeState.value = WhetherVerificationCode.FAIL
////                }.collectWithError {
////                    val bitmap = BitmapFactory.decodeByteArray(it, 0, it.size)
////                    refreshDialogVerificationCode.value = bitmap.asImageBitmap()
////                    refreshVerificationCodeState.value = WhetherVerificationCode.SUCCESS
////                }
//            }
        }
    }

//    fun refreshWithVerificationCode(verification:String){
//        viewModelScope.launch(Dispatchers.IO) {
//            refreshButtonState.logicIfNotLoading {
//                val passwordState = kValueAction.getSchoolPassword()
//                val usernameState = kValueAction.getCurrentWeek()
//                if (passwordState == null || usernameState == null){
//                    TODO()
//                    return@logicIfNotLoading
//                }
//                classSchedule
//                BlockLoginPageRepository.loginStudent(
//                    pass = passwordState,
//                    user = usernameState,
//                    captcha = verification,
//                    everyErrorAction = {
//                        refreshCourse()
//                        refreshButtonState.value = ButtonState.Normal
//                    }
//                )
//                    .flatMapConcat {
//                        BlockLoginPageRepository.loginByTokenForIdInUrl(
//                            result = it,
//                            failedToGetAccount = {
//                                easyToast(it.message.toString())
//                                refreshButtonState.value = ButtonState.Normal
//                            },
//                            elseMistake = { error ->
//                                easyToast(error.message.toString())
//                                refreshButtonState.value = ButtonState.Normal
//                            }
//                        ).retryWhen{ error, tryTime ->
//                            error.message == "获取account失败" && tryTime <= 3
//                        }
//                            .catchWithMassage {
//                                if(it.message == "获取account失败"){
//                                    refreshButtonState.value = ButtonState.Normal
//                                    easyToast(it.message.toString())
//                                }
//                                else{
//                                    refreshButtonState.value = ButtonState.Normal
//                                    easyToast(it.message.toString())
//                                }
//                            }.flowIO()
//                    }
//                    .flatMapConcat {
//                        BlockLoginPageRepository.loadCookieData(
//                            queryMap = it,
//                            user = usernameState
//                        )
//                    }
//                    .flatMapConcat {
//                        BlockLoginPageRepository.checkTheUserInformation(
//                            user = usernameState,
//                            serialNumberHandling = {
//
//                            }
//                        ).catchWithMassage {
//                            refreshButtonState.value = ButtonState.Normal
//                        }
//                    }
//                    .collectWithError{ loginResult ->
//                        when(loginResult){
//                            LoginResult.LoginError->{
//                                refreshButtonState.value = ButtonState.Normal
//                                easyToast("刷新失败")
//                            }
//                            LoginResult.LoginSuccess->{
//                                setUserDataStore(UserPreferencesKey.USER_DATA_VALIDITY_PERIOD,
//                                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
//                                easyToast("刷新成功")
//                                refreshButtonState.value = ButtonState.Normal
//                                getCourseFromNetwork()
//                            }
//                        }
//                    }
//            }
//        }
//        viewModelScope.launch(Dispatchers.IO) {
//            getExamData()
//        }
//    }

    fun changeCurrentYear(newValue:String){
        viewModelScope.launch {
            currentYear.emit(newValue)
            classScheduleRepository.apply {
                 classSchedule.getClassScheduleClient().getSchoolCalendar(newValue)
                     .retry(10)
                     .map { result->
                         parseBeginDate(newValue,result)
                     }
                     .collectWithMassage { label, data ->
                         TODO()
                     }
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

    fun parseExamsHTML(result: String): List<ExamBean> {
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
    private suspend fun parseBeginDate(xq: String, result: String) {
        val document = Ksoup.parse(result)
        val select = document.getElementsByTag("select")[0]
        val option = select.getElementsByAttributeValueStarting("value", xq)
        if (option.size > 0) {
            val value = option[0].attr("value")
            val beginYear = value.substring(6, 10).toInt()
            val beginMonth = value.substring(10, 12).toInt()
            val beginDay = value.substring(12, 14).toInt()
            kValueAction.apply {
                setDateStartMonth(beginMonth)
                setDateStartYear(beginYear)
                setDateStartDay(beginDay)
            }
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

