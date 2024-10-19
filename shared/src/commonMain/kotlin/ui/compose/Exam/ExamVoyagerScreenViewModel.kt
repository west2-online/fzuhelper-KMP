package ui.compose.Exam

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.fleeksoft.ksoup.Ksoup
import com.futalk.kmm.Exam
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import repository.JwchRepository
import util.flow.catchWithMessage
import util.flow.collectWithMessage
import util.flow.launchInDefault
import util.math.parseIntWithNull
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkSuccess
import util.network.resetWithLog
import util.network.resetWithoutLog

@OptIn(ExperimentalCoroutinesApi::class)
class ExamVoyagerScreenViewModel(
  private val kValueAction: UndergraduateKValueAction,
  private val jwchRepository: JwchRepository,
  private val jwch: Jwch,
  private val dao: Dao,
  private val shareClient: ShareClient,
): ViewModel() {
  val refreshState = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())
  val refreshExamState = MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend())

  val currentYear = kValueAction.currentYear
  var selectYear = MutableStateFlow(kValueAction.currentYear.value)
  val yearOptions =
    database.yearOptionsQueries.getAllYearOptions().asFlow().mapToList(Dispatchers.IO)
  var examToCourse = MutableStateFlow(kValueAction.examToCourse.currentValue.value)

  data class ExamAddressParse(
    val year: Int?,
    val month: Int?,
    val startHour: Int?,
    val startMinute: Int?,
    val endHour: Int?,
    val endMinute: Int?,
    val address: String?,
  )

  private fun (ExamAddressParse?).verify(): ExamAddressParse? {
    this ?: return null
    return if (
      year == null ||
      month == null ||
      startHour == null ||
      startMinute == null ||
      endHour == null ||
      endMinute == null ||
      address == null
    ) {
      null
    } else {
      this
    }
  }

  data class ExamForShow(val exam: Exam, var examAddressParse: ExamAddressParse?)

  val examList =
    database.examQueries.selectAllExams().asFlow().mapToList(Dispatchers.IO).map { exams ->
      exams
        .filter { it.address.isNotEmpty() }
        .map { exam ->
          // 定义正则表达式
          val datePattern =
            Regex("""(\d{4})年(\d{2})月(\d{2})日 (\d{2}):(\d{2})-(\d{2}):(\d{2}) (\S+)""")
              .matchEntire(exam.address)
          datePattern ?: return@map ExamForShow(exam, null)
          datePattern.groupValues.let {
            return@map ExamForShow(
              exam = exam,
              examAddressParse =
              ExamAddressParse(
                year = parseIntWithNull(it[1]),
                month = parseIntWithNull(it[2]),
                startHour = parseIntWithNull(it[3]),
                startMinute = parseIntWithNull(it[4]),
                endHour = parseIntWithNull(it[5]),
                endMinute = parseIntWithNull(it[6]),
                address = it[7],
              ),
            )
          }
        }
        .map { it.apply { examAddressParse = examAddressParse.verify() } }
    }

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

  /** 刷新考试数据 */
  fun refreshExamData() {
    viewModelScope.launchInDefault {
      refreshExamState.logicIfNotLoading {
//        val xq = kValueAction.currentXq.currentValue.value
        val studentData = jwch.getJwchClient()
        val client =
          studentData.first
            ?: run {
              refreshState.resetWithLog(
                logLabel = "登录失败",
                NetworkResult.Error(Throwable("登录失败"), Throwable("登录失败")),
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
          with(jwchRepository) {
            getExamStateHTML(id)
              .map {
                return@map (parseExamsHTML(it))
              }
              .collect {
                dao.examDao.insertExam(it)
                kValueAction.needFresh.setValue(1)
                refreshExamState.resetWithoutLog(networkSuccess("刷新考试记录成功"))
              }
          }
        }
      }
    }
  }

  /**
   * 是否是当前年
   *
   * @return Boolean
   */
  fun isCurrentYear(): Boolean {
    return kValueAction.currentYear.value == selectYear.value
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
      //            println(beginMonth)
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

  fun switchExamToCourse(): Unit {
    if (examToCourse.value==null) {
      examToCourse.value = 1
    }else{
      examToCourse.value = 1- examToCourse.value!!
    }
    viewModelScope.launchInDefault {
      kValueAction.examToCourse.setValue(examToCourse.value!!)
    }
  }
}

/**
 * 考试的信息
 *
 * @property name String 考试名
 * @property xuefen String 学分
 * @property teacher String 老师
 * @property address String 地址
 * @property zuohao String 座号
 * @constructor
 */
data class ExamBean(
  var name: String = "",
  var xuefen: String = "",
  var teacher: String = "",
  var address: String = "",
  var zuohao: String = "",
)
