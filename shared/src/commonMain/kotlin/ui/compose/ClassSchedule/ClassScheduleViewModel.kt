package ui.compose.ClassSchedule

import androidx.compose.foundation.ScrollState
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fleeksoft.ksoup.Ksoup
import com.futalk.kmm.CourseBean
import configureForPlatform
import dao.Dao
import dao.UndergraduateKValueAction
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import di.Jwch
import di.ShareClient
import di.database
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRedirect
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import kotlinx.datetime.isoDayNumber
import repository.CourseBeanForTemp
import repository.JwchRepository
import repository.WeekData
import ui.compose.Exam.ExamBean
import util.flow.actionWithLabel
import util.flow.catchWithMessage
import util.flow.collectWithMessage
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkError
import util.network.resetWithLog
import util.network.resetWithoutLog

/**
 * 课程功能的相关功能
 *
 * @property kValueAction UndergraduateKValueAction
 * @property jwchRepository JwchRepository
 * @property jwch Jwch
 * @property dao Dao
 * @property shareClient ShareClient
 * @property classScheduleUiState ClassScheduleUiState 用于渲染的开始年月日
 * @property currentYear StateFlow<String?> 当前学年
 * @property selectYear MutableStateFlow<String?> 用户选中的年份
 * @property selectWeek MutableStateFlow<Int> 用户选中的周数
 * @property scrollState ScrollState 滑动的state
 * @property courseDialog MutableStateFlow<CourseBean?> 课程dialog的显示
 * @property refreshState MutableStateFlow<NetworkResult<String>> 刷新课程的状态
 * @property refreshExamState MutableStateFlow<NetworkResult<String>> 刷新考试的状态
 * @property course Flow<List<CourseBean>> 从数据库中获取保存的课程
 * @property courseForShow StateFlow<List<CourseBean>> 根据用户选中学年计算需要显示的课程
 * @property yearOptions Flow<List<YearOptions>> 用户可选的学年
 * @property examList Flow<List<Exam>> 从数据库中得到的考试数据
 * @constructor
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ClassScheduleViewModel(
  private val kValueAction: UndergraduateKValueAction,
  private val jwchRepository: JwchRepository,
  private val jwch: Jwch,
  private val dao: Dao,
  private val shareClient: ShareClient,
) : ViewModel() {
  class ClassScheduleUiState(kValueAction: UndergraduateKValueAction) {
    val startYear = MutableStateFlow(kValueAction.dataStartYear.currentValue.value ?: 2023)
    val startMonth = MutableStateFlow(kValueAction.dataStartMonth.currentValue.value ?: 1)
    val startDay = MutableStateFlow(kValueAction.dataStartDay.currentValue.value ?: 1)
  }

  val classScheduleUiState = ClassScheduleUiState(kValueAction)

  val currentYear = kValueAction.currentYear
  var selectYear = MutableStateFlow(kValueAction.currentYear.value)
  var selectWeek = MutableStateFlow<Int>(kValueAction.currentWeek.currentValue.value ?: 1)

  val scrollState = ScrollState(initial = 0)
  val courseDialog = MutableStateFlow<CourseBean?>(null)
  val refreshState = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())

  private var course: Flow<List<CourseBean>> =
    database.classScheduleQueries.getAllCourse().asFlow().mapToList(Dispatchers.IO)

  val examToCourse = MutableStateFlow(kValueAction.examToCourse.currentValue)
  val needFresh = MutableStateFlow(kValueAction.needFresh.currentValue)

  val courseForShow =
    selectYear
      .combine(course) { currentYear, course ->
        course.filter { "${it.kcXuenian}0${it.kcYear}" == currentYear }
      }
      .stateIn(viewModelScope, SharingStarted.Lazily, listOf())

  val yearOptions =
    database.yearOptionsQueries.getAllYearOptions().asFlow().mapToList(Dispatchers.IO)

  /** 初始化会更新更新当前学期 */
  init {
    viewModelScope.launchInDefault {
      jwchRepository.apply {
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
              getSchoolCalendar(it.getXueQi()).retry(10).map { result ->
                parseBeginDateReset(it.getXueQi(), result, true)
              }
            }
            .catchWithMessage(label = "更新当前学期", action = null)
            .collect {}
        }
      }
    }
  }

  /**
   * 是否是当前周
   *
   * @param week Int
   * @return Boolean
   */
  fun isCurrentWeek(week: Int): Boolean {
    return kValueAction.currentWeek.currentValue.value == week &&
      kValueAction.currentYear.value == selectYear.value
  }

  /**
   * 是否是当前年
   *
   * @return Boolean
   */
  fun isCurrentYear(): Boolean {
    return kValueAction.currentYear.value == selectYear.value
  }

  /** Refresh class data 刷新课程信息 */
  @OptIn(ExperimentalCoroutinesApi::class)
  fun refreshClassData() {
    viewModelScope.launchInDefault {
      refreshState.logicIfNotLoading {
        jwchRepository.apply {
          val studentData = jwch.getJwchClient()
          val client =
            studentData.first
              ?: run {
                refreshState.resetWithLog(
                  logLabel = "登录失败",
                  NetworkResult.Error(Throwable("获取课程失败,请重试"), Throwable("登录失败")),
                )
                return@logicIfNotLoading
              }
          val id =
            studentData.second
              ?: run {
                refreshState.resetWithLog(
                  logLabel = "id有误",
                  NetworkResult.Error(Throwable("获取课程失败"), Throwable("id有误")),
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
              .catchWithMessage(
                label = "更新当前学期失败",
                action = { label, error ->
                  refreshState.resetWithLog(
                    logLabel = "更新当前学期失败",
                    NetworkResult.Error(Throwable("更新当前学期失败"), error),
                  )
                },
              )
              .flatMapConcat {
                getSchoolCalendar(it.getXueQi()).retry(10).map { result ->
                  parseBeginDateReset(it.getXueQi(), result)
                }
              }
              .actionWithLabel(
                label = "更新开学日期失败",
                catchAction = { label, error ->
                  refreshState.resetWithLog(label, networkError(error, "更新开学日期失败"))
                },
                collectAction = { label, data ->
                  getCourseFromNetwork(id)
                  getExamFromNetwork(id)
                                },
              )
          }
        }
      }
      kValueAction.needFresh.setValue(0)
    }
  }

  /**
   * 获取课程，只获取当前课程
   *
   * @param id String
   * @receiver HttpClient
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun HttpClient.getCourseFromNetwork(id: String) {
    with(jwchRepository) {
      with(this@getCourseFromNetwork) {
        getCourseStateHTML(id)
          .catchWithMessage { label, throwable ->
            refreshState.resetWithLog(label, networkError(throwable, "更新失败"))
          }
          .flatMapConcat { stateHTML ->
            getWeek().map { weekDataOnFlow ->
              CourseData(stateHTML = stateHTML, weekData = weekDataOnFlow)
            }
          }
          .catchWithMessage { label, throwable ->
            refreshState.resetWithLog(label, networkError(throwable, "更新失败"))
          }
          .collect { courseData ->
            val weekData = courseData.weekData
            val currentXq = "${weekData.curXueqi}0${weekData.curXuenian}"
            getCourses(currentXq, courseData.stateHTML)
              .flatMapConcat {
                getCoursesHTML(
                  it,
                  currentXq,
                  onGetOptions = { yearOptionsFromNetwork ->
                    dao.yearOpensDao.insertYearOpens(yearOptionsFromNetwork)
                  },
                  id,
                )
              }
              .catchWithMessage { label, throwable ->
                refreshState.resetWithLog(label, networkError(throwable, "更新失败"))
              }
              .collect { initCourseBean ->
                dao.classScheduleDao.insertClassScheduleByXueNian(
                  initCourseBean,
                  weekData.curXueqi,
                  weekData.curXuenian,
                )
                refreshState.resetWithoutLog(NetworkResult.Success("刷新成功"))
                getOtherCourseFromNetwork(currentXq, id)
              }
          }
      }
    }
  }

  /**
   * 获取非currentXq学期的课程
   *
   * @param currentXq String
   * @param id String
   * @receiver HttpClient
   */
  @OptIn(ExperimentalCoroutinesApi::class)
  private fun HttpClient.getOtherCourseFromNetwork(currentXq: String, id: String) {
    viewModelScope.launchInDefault {
      val yearOptions = dao.yearOpensDao.getAllYearOpens()
      yearOptions
        .filter { it.yearOptionsName != currentXq && it.yearOptionsName != "" }
        .map { it.yearOptionsName }
        .forEach { yearOptionsName ->
          yearOptionsName.let { xq ->
            with(jwchRepository) {
              this@getOtherCourseFromNetwork.getCourseStateHTML(id)
                .flatMapConcat { stateHtml -> getCourses(xq, stateHtml) }
                .flatMapConcat {
                  this@getOtherCourseFromNetwork.getCoursesHTML(it, xq, onGetOptions = {}, id)
                }
                .collect { initCourseBean ->
                  dao.classScheduleDao.insertClassScheduleByXueNian(
                    initCourseBean,
                    xq.substring(0, 4).toInt(),
                    xq.substring(5, 6).toInt(),
                  )
                }
            }
          }
        }
    }
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  private suspend fun HttpClient.getExamFromNetwork(id: String) {
    with(jwchRepository) {
      with(this@getExamFromNetwork) {
        getExamStateHTML(id)
          .catchWithMessage { label, throwable ->
            refreshState.resetWithLog(label, networkError(throwable, "更新失败"))
          }
          .map {
            return@map (parseExamsHTML(it))
          }
          .collect {
            dao.examDao.insertExam(it)
            it.filter { it.address.isNotEmpty() }
              .map { exam ->
                val matchedCourse =
                  database.classScheduleQueries.getAllCourse().asFlow().mapToList(Dispatchers.IO)
                    .flatMapConcat { courseList ->
                      flowOf(courseList.firstOrNull { it.kcName == exam.name })
                    }
                    .firstOrNull()
                // 定义正则表达式
                val datePattern =
                  Regex("""(\d{4})年(\d{2})月(\d{2})日 (\d{2}):(\d{2})-(\d{2}):(\d{2}) (\S+)""")
                    .matchEntire(exam.address)
                if (matchedCourse == null || datePattern == null || datePattern.groupValues.size < 9
                  || datePattern.groupValues.drop(1).any { it.isEmpty() }) {
                  return@map null
                }

                val week = getWeekOfYear(
                  datePattern.groupValues[1].toInt(),
                  datePattern.groupValues[2].toInt(),
                  datePattern.groupValues[3].toInt()
                )-getWeekOfYear(
                  classScheduleUiState.startYear.value,
                  classScheduleUiState.startMonth.value,
                  classScheduleUiState.startDay.value
                )+1

                val examCourse = CourseBeanForTemp()
                datePattern.groupValues.let {
                  examCourse.apply {
                    kcName = exam.name
                    kcLocation = it[8]
                    kcStartTime = startTime.entries.filter { time ->
                      time.key <= it[4].toInt() * 60 + it[5].toInt()
                    }.last().value
                    kcEndTime = endTime.entries.filter { time ->
                      time.key >= it[6].toInt() * 60 + it[7].toInt()
                    }.first().value
                    kcStartWeek = week
                    kcEndWeek = week
                    kcIsDouble = true
                    kcIsSingle = true
                    kcWeekend = LocalDate(
                      it[1].toInt(),
                      it[2].toInt(),
                      it[3].toInt()
                    ).dayOfWeek.isoDayNumber
                    kcYear = matchedCourse!!.kcYear.toInt()
                    kcXuenian = matchedCourse!!.kcXuenian.toInt()
                    kcNote = "${it[4]}:${it[5]}-${it[6]}:${it[7]}"
                    kcBackgroundId = matchedCourse!!.kcBackgroundId.toInt()
                    shoukeJihua = matchedCourse!!.shoukeJihua
                    jiaoxueDagang = matchedCourse!!.jiaoxueDagang
                    teacher = exam.teacher
                    priority = matchedCourse!!.priority + 1
                    type = 1
                  }
                  dao.classScheduleDao.insertExamSchedule(examCourse)
                }
              }
          }
      }
    }
  }

  /**
   * 解析得到的考试html
   *
   * @param result String
   * @return List<ExamBean>
   */
  private fun parseExamsHTML(result: String): List<ExamBean> {
    val exams = ArrayList<ExamBean>()
    val document = Ksoup.parse(result)
    val examElements =
      document
        .select("table[id=ContentPlaceHolder1_DataList_xxk]")
        .select(
          "tr[style=height:30px; border-bottom:1px solid gray; border-left:1px solid gray; vertical-align:middle;]"
        )
    println("getExamInfo: examList:" + examElements.size)
    for (i in examElements.indices) {
      val element = examElements[i]
      val tds = element.select("td")
      val name = tds[0].text()
      val xuefen = tds[1].text()
      val teacher = tds[2].text()
      val address = tds[3].text()
      val zuohao = tds[4].text()
      if (address.isNotEmpty()) {}

      val exam = ExamBean(name, xuefen, teacher, address, zuohao)
      exams.add(exam)
    }
    return exams
  }

  /**
   * 更改当前学年
   *
   * @param newValue String
   */
  fun changeCurrentYear(newValue: String) {
    viewModelScope.launchInDefault {
      selectYear.emit(newValue)
      jwchRepository.apply {
        val client =
          HttpClient() {
            install(ContentNegotiation) { json() }
            install(HttpCookies) {}
            install(HttpRedirect) { checkHttpMethod = false }
            configureForPlatform()
          }
        client
          .getSchoolCalendar(newValue)
          .retry(10)
          .map { result ->
            return@map parseBeginDateReset(newValue, result)
          }
          .collectWithMessage { label, data -> }
      }
    }
  }

  data class CourseData(val stateHTML: String, val weekData: WeekData)

  /**
   * 解析开学时间网页
   *
   * @param result 获取到的网页
   */
  private suspend fun parseBeginDateReset(xq: String, result: String, save: Boolean = false) {
    val document = Ksoup.parse(result)
    val select = document.getElementsByTag("select")[0]
    val option = select.getElementsByAttributeValueStarting("value", xq)
    if (option.size > 0) {
      val value = option[0].attr("value")
      val beginYear = value.substring(6, 10).toInt()
      val beginMonth = value.substring(10, 12).toInt()
      val beginDay = value.substring(12, 14).toInt()
      if (save) {
        kValueAction.apply {
          dataStartDay.setValue(beginDay)
          dataStartMonth.setValue(beginMonth)
          dataStartYear.setValue(beginYear)
        }
      }
      classScheduleUiState.startDay.emit(beginDay)
      classScheduleUiState.startYear.emit(beginYear)
      classScheduleUiState.startMonth.emit(beginMonth)
      //                val calendar = Calendar.getInstance()
      //                calendar.set(beginYear, beginMonth - 1, beginDay, 0, 0, 0)
      // 存储当前设置的学期开学时间
      //                DataManager.beginDate = calendar.timeInMillis
      // 根据开学时间和当前时间计算出当前周数
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

  fun getWeekOfYear(year: Int, month: Int, day: Int): Int {
    val epochDay = LocalDate(year, month, day).toEpochDays()
    val firstDayOfYear = LocalDate(year, Month.JANUARY, 1).toEpochDays()
    return (epochDay - firstDayOfYear) / 7 + 1
  }
}

val startTime=mapOf<Int,Int>(
  8*60+20 to 1,
  9*60+15 to 2,
  10*60+20 to 3,
  11*60+15 to 4,
  14*60 to 5,
  14*60+55 to 6,
  15*60+50 to 7,
  16*60+45 to 8,
  19*60 to 9,
  19*60+55 to 10,
  20*60+50 to 11,
)

val endTime=mapOf<Int,Int>(
  8*60+45+45 to 1,
  9*60+15+45 to 2,
  10*60+20+45 to 3,
  11*60+15+45 to 4,
  14*60+45 to 5,
  14*60+55+45 to 6,
  15*60+50+45 to 7,
  16*60+45+45 to 8,
  19*60+45 to 9,
  19*60+55+45 to 10,
  20*60+50+45 to 11,
)
