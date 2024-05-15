package repository

import com.fleeksoft.ksoup.Ksoup
import config.JWCH_BASE_URL
import config.SCHOOL_CALENDAR_URL
import data.classSchedule.GetVerifyCodeFormWest2
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.http.Parameters
import io.ktor.utils.io.charsets.Charset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import util.math.parseInt

class ClassScheduleRepository {
    val charSet = Charset.forName("GB2312")

    //登录第一步
    suspend fun HttpClient.loginStudent(
        user: String,
        pass: String,
        verifyCode: String,
    ): Flow<HttpResponse> {
        return flow{
            val response = this@loginStudent.submitForm(
                url = "https://jwcjwxt1.fzu.edu.cn/logincheck.asp",
                formParameters = Parameters.build {
                    append("muser", user)
                    append("passwd", pass)
                    append("VerifyCode", verifyCode)
                }
            ) {
                headers {
                    append("Referer", "https://jwch.fzu.edu.cn/html/login/1.html")
                    append("Origin", "https://jwch.fzu.edu.cn")
                }
            }
            emit(response)
        }
    }

    //识别验证码
    suspend fun HttpClient.parseVerifyCodeFormWest2(
        verifyCodeForParse:String
    ): Flow<String> {
        return flow {
            val verifyCode = this@parseVerifyCodeFormWest2.submitForm (
                formParameters = Parameters.build {
                    append("validateCode",verifyCodeForParse)
                },
                url = "https://statistics.fzuhelper.w2fzu.com/api/login/validateCode"
            ).body<GetVerifyCodeFormWest2>().message
            emit(verifyCode)
        }
    }

    //获取验证码
    suspend fun HttpClient.getVerifyCode():Flow<ByteArray>{
        return flow {
            val verifyCodeForParse = this@getVerifyCode.get("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp").readBytes()
            emit(verifyCodeForParse)
        }
    }


    suspend fun HttpClient.getExamStateHTML(id: String): Flow<String> {
        return flow {
            val data = this@getExamStateHTML.get("https://jwcjwxt2.fzu.edu.cn:81/student/xkjg/examination/exam_list.aspx"){
                url {
                    parameters.append("id",id)
                }
            }.bodyAsText()
            emit(data)
        }
    }

    //用token登录 登录第二步
    suspend fun HttpClient.loginByToken(token: String): Flow<JwchTokenLoginResponseDto> {
        return flow {
            val response = this@loginByToken.submitForm(
                url = "https://jwcjwxt2.fzu.edu.cn/Sfrz/SSOLogin",
                formParameters = Parameters.build {
                    append("token", token)
                }
            ) {
                headers {
                    append("X-Requested-With", "XMLHttpRequest")
                }
            }.readBytes().decodeToString()
            emit(Json.decodeFromString<JwchTokenLoginResponseDto>(response))
        }
    }

    //登录第三步
    suspend fun HttpClient.loginCheckXs(id: String,num:String): Flow<HttpResponse> {
        return flow {
            val response = this@loginCheckXs.get("https://jwcjwxt2.fzu.edu.cn:81/loginchk_xs.aspx") {
                url { url ->
                    mapOf(
                        "id" to id,
                        "num" to num,
                        "ssourl" to "https://jwcjwxt2.fzu.edu.cn",
                        "hosturl" to "https://jwcjwxt2.fzu.edu.cn:81"
                    ).forEach {
                        parameters.append(it.key,it.value)
                    }
                }
            }
            emit(response)
        }
    }

    suspend fun HttpClient.getCourseState(id: String):Flow<HttpResponse>{
        return flow {
            val response = this@getCourseState.get ("${JWCH_BASE_URL}/student/xkjg/wdxk/xkjg_list.aspx"){
                url{
                    parameters.append("id",id)
                }
            }
            emit(response)
        }
    }

    //获取课程
    private suspend fun HttpClient.getCourses(
        id: String,
        xuenian: String,
        event: String,
        state: String
    ):Flow<HttpResponse>{
        return flow{
            val response = this@getCourses.submitForm(
                "${JWCH_BASE_URL}/student/xkjg/wdxk/xkjg_list.aspx",
                formParameters = Parameters.build {
                    append("ctl00\$ContentPlaceHolder1\$DDL_xnxq", xuenian)
                    append("__EVENTVALIDATION", event)
                    append("__VIEWSTATE", state)
                    append("ctl00\$ContentPlaceHolder1\$BT_submit", "确定")
                }
            ){
                url {
                    parameters.append("id",id)
                }
            }
            emit(response)
        }
    }

    suspend fun HttpClient.getCourses(xq:String,stateHTML:String):Flow<Map<String,String>>{
        return flow {
            val viewStateMap = parseCourseStateHTML(stateHTML)
            emit(viewStateMap)
        }
    }

    suspend fun HttpClient.getCoursesHTML(
        viewStateMap:Map<String,String>,
        xq: String,onGetOptions : (List<String>)->Unit = {},
        id: String
    ):Flow<List<CourseBeanForTemp>>{
        return this@getCoursesHTML.getCourses(
            id,
            xq,
            viewStateMap["EVENTVALIDATION"] ?: "",
            viewStateMap["VIEWSTATE"] ?: ""
        ).map {
            val result = it.bodyAsText(charSet)
            parseCoursesHTML(xq, result,onGetOptions=onGetOptions)
        }
    }


    suspend fun HttpClient.getCourseStateHTML(
        id : String
    ): Flow<String> {
        return flow {
            val response = this@getCourseStateHTML.get("${JWCH_BASE_URL}/student/xkjg/wdxk/xkjg_list.aspx"){
                url{
                    parameters.append("id",id)
                }
            }.bodyAsText(Charset.forName("GB2312"))
            emit(response)
        }
    }

    //获取当前学期信息
    suspend fun HttpClient.getWeek():Flow<WeekData>{
        return flow {
            val response = this@getWeek.get("https://jwcjwxt2.fzu.edu.cn:82/week.asp").bodyAsText(Charset.forName("GB2312"))
            emit(response)
        }.map {
            parseWeekHTML(it)
        }
    }

    //获取某个学期的起始日期
    fun HttpClient.getSchoolCalendar(xq: String): Flow<String> {
        return flow {
            val response = this@getSchoolCalendar.get("$SCHOOL_CALENDAR_URL/xl.asp"){
                url {
                    parameters.append("xq",xq)
                }
            }.bodyAsText(Charset.forName("GB2312"))
            emit(response)
        }
    }

    private fun parseWeekHTML(result: String): WeekData {
        val nowWeek = result.split("var week = \"")[1].split("\";")[0].toInt()
        val curXuenian = result.split("var xq = \"")[1].split("\";")[0].toInt()
        val curYear = result.split("var xn = \"")[1].split("\";")[0].toInt()
        return WeekData(
            nowWeek = nowWeek,
            curXuenian = curXuenian,
            curXueqi = curYear
        )
    }

    private fun parseCoursesHTML(
        xueNian: String,
        result: String,
        onGetOptions : (List<String>)->Unit = {}
    ): List<CourseBeanForTemp> {
        val tempCourses = ArrayList<CourseBeanForTemp>()
        //解析学年
        val yearStr = xueNian.substring(0, 4)
        val xuenianStr = xueNian.substring(4, 6)
        val year = parseInt(yearStr)
        val xuenian = parseInt(xuenianStr)
        val document = Ksoup.parse(result)
        //添加学期列表
        val options = document.select("option")
        val optionStr = ArrayList<String>()
        for (element in options) {
            optionStr.add(element.attr("value"))
        }
        onGetOptions.invoke(optionStr.toList())

//        if (optionStr.isEmpty()) {
//            throw ApiException("term is empty!")
//        }
//        DataManager.options = optionStr

        val removeLocationPrefix = fun(location: String) = location
            .removePrefix("铜盘")
            .removePrefix("旗山")

        //开始解析课表
        val courseEles =
            document.select("tr[onmouseover=c=this.style.backgroundColor;this.style.backgroundColor='#CCFFaa']")
        for (i in courseEles.indices) {
            val kb = courseEles[i]
            val kclb = kb.select("td")[6].text()
            //免听直接跳过不添加
            if (kclb.contains("免听")) continue
            val titleEle = kb.select("td")[1]
            val title = titleEle.text()
            val jiaoxueDagang = kb.select("td")[2].select("a")[0].attr("href")
                .replace("javascript:pop1('", JWCH_BASE_URL).replace("');", "")
                .split("&")[0] + "&id={id}"
            val shoukeJihua = kb.select("td")[2].select("a")[1].attr("href")
                .replace("javascript:pop1('", JWCH_BASE_URL).replace("');", "")
                .split("&")[0] + "&id={id}"
            val teacher = kb.select("td")[7].text()
            //解析课程备注:
            val note = kb.select("td")[11].text()

            //解析上课时间
            val timeCou = kb.select("td")[8].html()
            val strings = timeCou.split("<br>").dropLastWhile { it.isEmpty() }.toTypedArray()
            for (string in strings) {
                val kc = CourseBeanForTemp()
                if (note.isNotEmpty()) {
                    kc.kcNote = note
                }
                kc.teacher = teacher
                kc.jiaoxueDagang = jiaoxueDagang
                kc.shoukeJihua = shoukeJihua
                kc.kcBackgroundId = i
                kc.kcYear = year
                kc.kcXuenian = xuenian
                kc.kcName = title
                try {
                    val contents = string.split("&nbsp;")
                    val week = contents[0].split("-")
                    val startWeek = parseInt(week[0].replace("\n","").replace(" ",""))
                    val endWeek = parseInt(week[1])
                    kc.kcStartWeek = startWeek
                    kc.kcEndWeek = endWeek
                    val weekend = parseInt(contents[1].substring(2, 3))
                    kc.kcWeekend = weekend

                    when {
                        contents[1].contains("单") -> {
                            val timeStr = contents[1].substring(4, contents[1].length - 4)
                            val time = timeStr.split("-")
                            val startTime = parseInt(time[0])
                            val endTime = parseInt(time[1])
                            kc.kcStartTime = startTime
                            kc.kcEndTime = endTime
                            kc.kcIsDouble = false

                        }
                        contents[1].contains("双") -> {
                            val timeStr = contents[1].substring(4, contents[1].length - 4)
                            val time = timeStr.split("-")
                            val startTime = parseInt(time[0])
                            val endTime = parseInt(time[1])
                            kc.kcStartTime = startTime
                            kc.kcEndTime = endTime
                            kc.kcIsSingle = false
                        }
                        else -> {
                            val timeStr = contents[1].substring(4, contents[1].length - 1)
                            val time = timeStr.split("-")
                            val startTime = parseInt(time[0])
                            val endTime = parseInt(time[1])
                            kc.kcStartTime = startTime
                            kc.kcEndTime = endTime
                        }
                    }

                    val location = contents[2]
                    kc.kcLocation = removeLocationPrefix(location)
                    tempCourses.add(kc)
                } catch (e: Exception) {
                    error("解析出错:$title")
                }
            }
            if (note.isNotEmpty()) {
                //解析调课信息
                var matcher =
                    Regex("(\\d{2})周 星期(\\d):(\\d{1,2})-(\\d{1,2})节\\s*调至\\s*(\\d{2})周 星期(\\d):(\\d{1,2})-(\\d{1,2})节\\s*(\\S*)")
                        .matchEntire(note)
                while (matcher!=null) {
                    matcher.groupValues.get(5)
                    val toWeek = matcher.groupValues[5].toIntOrNull() ?: 0
                    val toWeekend = matcher.groupValues[6].toIntOrNull() ?: 0
                    val toStart = matcher.groupValues[7].toIntOrNull() ?: 0
                    val toEnd = matcher.groupValues[8].toIntOrNull() ?: 0
                    val toPlace = matcher.groupValues[9] ?: ""
                    val kc = CourseBeanForTemp()
                    kc.kcNote = note
                    kc.teacher = teacher
                    kc.jiaoxueDagang = jiaoxueDagang
                    kc.shoukeJihua = shoukeJihua
                    kc.kcBackgroundId = i
                    kc.kcYear = year
                    kc.kcXuenian = xuenian
                    kc.kcStartWeek = toWeek
                    kc.kcEndWeek = toWeek
                    kc.kcStartTime = toStart
                    kc.kcEndTime = toEnd
                    kc.kcIsSingle = true
                    kc.kcIsDouble = true
                    kc.kcLocation = removeLocationPrefix(toPlace)
                    kc.kcWeekend = toWeekend
                    kc.kcName = "[调课]$title"
                    tempCourses.add(kc)
                    matcher = matcher.next()
                }
            }
        }
        return tempCourses
    }


    private fun parseCourseStateHTML(result: String): Map<String, String> {
        val document = Ksoup.parse(result)
        //设置常用参数
        val VIEWSTATE = document.getElementById("__VIEWSTATE")?.attr("value")
        val EVENTVALIDATION = document.getElementById("__EVENTVALIDATION")?.attr("value")
        val params = HashMap<String, String>()
        params["VIEWSTATE"] = VIEWSTATE!!
        params["EVENTVALIDATION"] = EVENTVALIDATION!!
        return params
    }
}


data class WeekData(
    val nowWeek : Int,
    val curXuenian: Int,
    val curXueqi: Int
){
    fun getXueQi():String{
        return "${this.curXueqi}0${this.curXuenian}"
    }

    override fun equals(other: Any?): Boolean {
        return "${this.curXuenian}${this.curXuenian}${this.curXuenian}" ==  "${(other as WeekData).curXuenian}${(other as WeekData).curXuenian}${(other as WeekData).curXuenian}"
    }

    override fun hashCode(): Int {
        var result = nowWeek
        result = 31 * result + curXuenian
        result = 31 * result + curXueqi
        return result
    }
}


data class CourseBeanForTemp(
    var courseId: Long = 0,
    var kcName: String = "",
    var kcLocation: String = "",
    var kcStartTime: Int = 0,
    var kcEndTime: Int = 0,
    var kcStartWeek: Int = 0,
    var kcEndWeek: Int = 0,
    var kcIsDouble: Boolean = true,
    var kcIsSingle: Boolean = true,
    var kcWeekend: Int = 0,
    var kcYear: Int = 0,
    var kcXuenian: Int = 0,
    var kcNote: String = "",
    var kcBackgroundId: Int = 0,
    var shoukeJihua: String = "",
    var jiaoxueDagang: String = "",
    var teacher: String = "",
    var priority: Long = 0,
    var type: Int = 0
)


@Serializable
data class JwchTokenLoginResponseDto(
    var code: Int, // 200
    var info: String, // 登录成功
    var data : Data
)


@Serializable
class Data()