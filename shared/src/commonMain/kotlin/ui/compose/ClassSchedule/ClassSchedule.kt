package ui.compose.ClassSchedule


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.futalk.kmm.CourseBean
import com.futalk.kmm.YearOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import util.compose.ScrollSelection
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassSchedule(
    classScheduleViewModel: ClassScheduleViewModel = koinInject<ClassScheduleViewModel>(),
    openDrawer : ()->Unit = {}
){
    val pageNumber = remember {
        mutableStateOf(30)
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        pageNumber.value
    }
//    val sidebarSlideState = viewModel.scrollState
    val courseDialog by classScheduleViewModel.courseDialog.collectAsState()
    val academicYearSelectsDialogState by classScheduleViewModel.academicYearSelectsDialogState.collectAsState()
    val refreshDialogState by classScheduleViewModel.refreshDialog.collectAsState()
    val refreshDialogVerificationCode = classScheduleViewModel.refreshDialogVerificationCode.collectAsState()
    val yearOptionsBean by classScheduleViewModel.yearOptions.collectAsState(listOf())
    val currentWeek by classScheduleViewModel.currentWeek.collectAsState()
    LaunchedEffect(currentWeek){
        pagerState.animateScrollToPage(classScheduleViewModel.currentWeek.value - 1)
    }
    LaunchedEffect(Unit){
        classScheduleViewModel.refreshInitData()
    }
    Column {
        TopAppBar(
            navigationIcon = {

            },
            title = {
                Text(text = "第${pagerState.currentPage + 1}周")
            },
            actions = {
                IconButton(onClick = {
                    openDrawer.invoke()
                }) {
                    Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = null)
                }
                IconButton(onClick = {
                    classScheduleViewModel.refreshCourse()
                }) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
                var expanded by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier
                        .wrapContentSize()
                ) {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "学年") },
                            onClick = {
                                expanded = false
                                classScheduleViewModel.academicYearSelectsDialogState.value = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Edit,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = {
                                Text(classScheduleViewModel.currentYear.collectAsState().value.toString(),
                                    textAlign = TextAlign.Center)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Settings") },
                            onClick = { /* Handle settings! */ },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Settings,
                                    contentDescription = null
                                )
                            })
                        DropdownMenuItem(
                            text = { Text("Send Feedback") },
                            onClick = { /* Handle send feedback! */ },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Email,
                                    contentDescription = null
                                )
                            },
                            trailingIcon = { Text("F11", textAlign = TextAlign.Center) })
                    }
                }
            }
        )
        TimeOfWeekColumn(
            week = pagerState.currentPage,
            startMonthState = classScheduleViewModel.classScheduleUiState.selectMonth.collectAsState(),
            startYearState = classScheduleViewModel.classScheduleUiState.selectYear.collectAsState(),
            startDayState = classScheduleViewModel.classScheduleUiState.selectDay.collectAsState()
        )
        Row (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ){
            Surface(modifier = Modifier
                .padding(top = 20.dp)
                .wrapContentWidth()
                .fillMaxHeight()){
                Sidebar(
                    classScheduleViewModel.scrollState
                )
            }
            HorizontalPager(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                state = pagerState,
            ){ page->
                Column {
                    TimeOfMonthColumn(
                        week = pagerState.currentPage + 1,
                        startMonthState = classScheduleViewModel.classScheduleUiState.selectMonth.collectAsState(),
                        startYearState = classScheduleViewModel.classScheduleUiState.selectYear.collectAsState(),
                        startDayState = classScheduleViewModel.classScheduleUiState.selectDay.collectAsState())
                    Row(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(classScheduleViewModel.scrollState)
                    ) {
                        WeekDay.values().forEachIndexed { weekIndex, value ->
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .requiredHeight((11 * 75).dp)
                            ) {
                                classScheduleViewModel.courseForShow.collectAsState().value.let { courseBeans ->
                                    courseBeans
                                        .filter {
                                            it.kcStartWeek <= page+1 && it.kcEndWeek >= page+1 && (it.kcIsDouble.toInt() == 1 && ((page+1)%2 == 0) || it.kcIsSingle.toInt() == 1 && ((page+1)%2 == 1))
                                        }
                                        .filter { courseBeanData ->
                                            courseBeanData.kcWeekend.toInt() == weekIndex + 1
                                        }.sortedBy { courseBean ->
                                            courseBean.kcStartTime
                                        }.let {
                                            it.forEachIndexed { index, item ->
                                                if (index == 0) {
                                                    EmptyClassCard(
                                                        item.kcStartTime - 1
                                                    )
                                                } else {
                                                    EmptyClassCard(
                                                        it[index].kcStartTime - it[index - 1].kcEndTime - 1
                                                    )
                                                }
                                                if(index == 0){
                                                    ClassCard(
                                                        item,
                                                        detailAboutCourse = {
                                                            classScheduleViewModel.courseDialog.value = it
                                                        }
                                                    )
                                                } else if(it[index].kcStartTime > it[index - 1].kcEndTime){
                                                    ClassCard(
                                                        item,
                                                        detailAboutCourse = {
                                                            classScheduleViewModel.courseDialog.value = it
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    courseDialog?.let {
        ClassDialog(
            courseBean = it,
            onDismissRequest = {
                classScheduleViewModel.courseDialog.value = null
            }
        )
    }
    if(academicYearSelectsDialogState){
        AcademicYearSelectsDialog(
            onDismissRequest = {
                classScheduleViewModel.academicYearSelectsDialogState.value = false
            },
            commit = {
                classScheduleViewModel.changeCurrentYear(it)
            },
            list = yearOptionsBean
        )
    }
}



@Composable
fun ClassCard(
    courseBean: CourseBean,
    detailAboutCourse:(CourseBean)->Unit = {}
) {
    Column (
        modifier = Modifier
            .height(
                ((courseBean.kcEndTime - courseBean.kcStartTime + 1) * 75).toInt().dp
            )
            .fillMaxWidth()
            .padding(vertical = 2.dp, horizontal = 2.dp)
            .clip(RoundedCornerShape(5.dp))
            .clickable {
                detailAboutCourse.invoke(courseBean)
            }
            .padding(vertical = 3.dp, horizontal = 3.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = courseBean.kcName,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            fontSize = 10.sp,
            lineHeight = 11.sp
        )
        Text(
            text = courseBean.kcLocation,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = 3.dp)
                .wrapContentHeight(),
            fontSize = 10.sp,
            lineHeight = 11.sp
        )
//        Text(
//            text = "203",
//            maxLines = 3,
//            overflow = TextOverflow.Ellipsis,
//            textAlign = TextAlign.Center
//        )
    }
}


@Composable
fun EmptyClassCard(
    weight: Long = 0
){
    LaunchedEffect(Unit){

    }
    Column (
        modifier = Modifier
            .height((75 * if (weight > 0) weight.toInt() else 0).dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(5.dp))
            .background(Color.Transparent)
            .padding(vertical = 10.dp, horizontal = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){

    }
}

//@Composable
//@Preview(device = "spec:width=200px,height=2340px,dpi=440")
//fun ClassCardPreview(){
//    ClassCard()
//}

@Composable
fun Sidebar(
    sidebarSlideState: ScrollState = rememberScrollState()
){
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(20.dp)
            .verticalScroll(
                state = sidebarSlideState
            ),
    ){
        (1..11).forEachIndexed { _, item ->
            Box(
                modifier = Modifier
                    .height(75.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = item.toString(),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    fontSize = 10.sp
                )
            }
        }
    }
}






//@Composable
//@Preview
//fun ClassDialogPreview(){
//    val showDialog = remember {
//        mutableStateOf(true)
//    }
//    ClassDialog(title = "", message = "", showClassDialog = showDialog) {
//        showDialog.value = false
//    }
//}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassDialog(
    courseBean: CourseBean,
    backgroundColor: Color = Color(217, 217, 239),
    onDismissRequest: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
        ){
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TopAppBar(
                    title = { /*TODO*/ },
                    actions = {
                        IconButton(onClick = { onDismissRequest.invoke() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .aspectRatio(1f)
                                    .padding(10.dp)
                            )
                        }
                    },
                    modifier = Modifier
                        .height(50.dp),
                )
                Text(
                    text = courseBean.kcName,
                    color = Color.Blue,
                    style = TextStyle(fontSize = 27.sp),
                    modifier = Modifier
                        .padding(horizontal = 10.dp),
                    textAlign = TextAlign.Center
                )
                LazyColumn(
                    modifier = Modifier
                        .padding(vertical = 20.dp)
                        .weight(1f)
                        .fillMaxWidth(0.7f)
                ) {
                    ClassScheduleNotificationDisplayProperties.forEachIndexed { index, item ->
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = if (index != 0) 10.dp else 0.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    modifier = Modifier
                                        .weight(3f),
                                    textAlign = TextAlign.End,
                                    text = item
                                )
                                Text(
                                    modifier = Modifier
                                        .padding(start = 20.dp)
                                        .weight(6f)
                                        .wrapContentHeight(),
                                    textAlign = TextAlign.Start,
                                    text = when (index) {
                                        0 -> courseBean.kcLocation
                                        1 -> courseBean.teacher
                                        2 -> "${courseBean.kcStartTime}~${courseBean.kcEndTime}"
                                        3 -> "${courseBean.kcStartWeek}周~${courseBean.kcEndWeek}周"
                                        4 -> if (courseBean.kcNote == "") "无" else courseBean.kcNote
                                        else -> ""
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 40.dp, vertical = 10.dp)
                ) {
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                    ) {

                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                    ) {

                    }
                }
            }
        }
    }
}



@Composable
fun TimeOfWeekColumn(
    week:Int,
    startMonthState: State<Int>,
    startYearState: State<Int>,
    startDayState: State<Int>
){
    val startMonth = startMonthState.value
    val startYear = startYearState.value
    val startDay = startDayState.value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            modifier = Modifier
                .width(20.dp)
                .wrapContentHeight(),
            text = "${getMonthByWeek(week,startYear,startMonth,startDay)}",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        WeekDay.values().forEach { item ->
            Text(
                text = item.chineseName,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun TimeOfMonthColumn(
    week:Int,
    startMonthState: State<Int>,
    startYearState: State<Int>,
    startDayState: State<Int>
){
    val startMonth = startMonthState.value
    val startYear = startYearState.value
    val startDay = startDayState.value
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WeekDay.values().forEachIndexed{ index,_ ->
            Text(
                text = getDataByWeek(week,index, startYear,startMonth,startDay).toString(),
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                textAlign = TextAlign.Center
            )
        }
    }
}


//fun getDataByWeek(week: Int,day:Int,startYear:Int, startMonth:Int,startDay:Int,):Int{
//    //创建一个自定义年月日的日期，使用Calendar.set
//    val calendar = Calendar.getInstance()
//    calendar.set(startYear,startMonth-1,startDay)
//    calendar.add(Calendar.WEEK_OF_YEAR,week-1)
//    calendar.add(Calendar.DAY_OF_MONTH,day)
//    val year = calendar.get(Calendar.YEAR)
//    val month = calendar.get(Calendar.MONTH)+1
//    val day = calendar.get(Calendar.DAY_OF_MONTH)
//    return day
//}


fun getDataByWeek(week: Int, day: Int, startYear: Int, startMonth: Int, startDay: Int, ): Int {
    //创建一个自定义年月日的日期，使用Calendar.set
    val time = LocalDateTime(startYear, startMonth, startDay, 16, 57, 0, 0).toInstant(TimeZone.of("UTC+8"))
        .plus(((week - 1) * 7).toDuration(DurationUnit.DAYS))
        .plus(day.toDuration(DurationUnit.DAYS))
    return time.toLocalDateTime(TimeZone.of("UTC+8")).dayOfMonth
}


fun getMonthByWeek(week: Int,startYear:Int, startMonth:Int,startDay:Int):Int{
    //创建一个自定义年月日的日期，使用Calendar.set
    val time = LocalDateTime(startYear, startMonth-1, startDay, 16, 57, 0, 0).toInstant(TimeZone.of("UTC+8"))
        .plus(((week - 1) * 7).toDuration(DurationUnit.DAYS))

    return time.toLocalDateTime(TimeZone.of("UTC+8")).monthNumber + 1
}

val ClassScheduleNotificationDisplayProperties = listOf("教室","教师","节数","周数","备注",)

enum class WeekDay(val chineseName: String, val englishName: String) {
    MONDAY("星期一", "Monday"),
    TUESDAY("星期二", "Tuesday"),
    WEDNESDAY("星期三", "Wednesday"),
    THURSDAY("星期四", "Thursday"),
    FRIDAY("星期五", "Friday"),
    SATURDAY("星期六", "Saturday"),
    SUNDAY("星期日", "Sunday"),
}

@Composable
fun AcademicYearSelectsDialog(
    onDismissRequest: ()->Unit = {},
    list: List<YearOptions> = listOf("1","2","3")
        .map { year ->
            YearOptions(
                yearOptionsName = year,
                yearOptionsId = 1
            )
        },
    commit: (String) -> Unit
){
    var data = MutableStateFlow("null")

    LaunchedEffect(Unit){
        data.value = if(list.isNotEmpty()) list[0].yearOptionsName.toString() else "null"
    }

    val state = rememberLazyListState()

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
        ) {

            ScrollSelection(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                textList = list
                    .map { year ->
                        year.yearOptionsName
                    },
                backgroundContent = {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(10.dp))
                    ){

                    }
                },
                onItemSelected = { _ ,item->
                    data.value = item
                },
                state = state
            )
            ElevatedButton(
                onClick = {
                    commit.invoke(data.value)
                    onDismissRequest.invoke()
                },
                enabled = data.collectAsState().value != "null",
                modifier = Modifier
                    .padding(top = 20.dp),
                contentPadding = PaddingValues(vertical = 10.dp, horizontal = 30.dp)
            ) {
                Text(text = "确定")
            }
        }
    }
}


//@Composable
//fun ToRefreshDialog(
//    onDismissRequest : ()->Unit = {},
//    verificationCodeState:State<WhetherVerificationCode> = remember {
//        mutableStateOf(WhetherVerificationCode.FAIL)
//    },
//    verificationCodeOnValueChange:(String) ->Unit = {
//
//    },
//    verificationCodeText:State<String> =  remember {
//        mutableStateOf("")
//    },
//    verificationCode: State<ImageBitmap?> = remember {
//        mutableStateOf(null)
//    },
//    retryGetVerificationCode: ()->Unit={},
//    refresh: ()->Unit={},
//    buttonState: State<ButtonState> = remember {
//        mutableStateOf(ButtonState.Normal)
//    },
//    clickAble : State<Boolean> = remember {
//        mutableStateOf(false)
//    }
//){
//    Dialog(onDismissRequest = onDismissRequest ) {
//        Surface(
//            modifier = Modifier
//                .fillMaxHeight(0.7f)
//                .clip(RoundedCornerShape(10.dp))
//        ){
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .verticalScroll(rememberScrollState())
//                    .padding(10.dp),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                CaptchaLine(
//                    verificationCodeState = verificationCodeState,
//                    verificationCodeOnValueChange = verificationCodeOnValueChange,
//                    verificationCodeText = verificationCodeText,
//                    verificationCode = verificationCode,
//                    retryGetVerificationCode = retryGetVerificationCode
//                )
//                LoadableButton(
//                    modifier = Modifier
//                        .padding(top = 20.dp),
//                    onClick = refresh,
//                    buttonState = buttonState.value,
//                    normalContent = {
//                        Text(text = "REFRESH")
//                    },
//                    enabled = buttonState.value == ButtonState.Normal && verificationCodeText.value != "",
//                    loadingContent = {
//                        CircularProgressIndicator(
//                            modifier = Modifier
//                                .fillMaxSize()
//                                .padding(10.dp)
//                        )
//                    }
//                )
//            }
//        }
//    }
//}

data class CourseBeanForShow(
     val courseId: Long,
     val kcName: String,
     val kcLocation: String,
     val kcStartTime: Long,
     val kcEndTime: Long,
     val kcStartWeek: Long,
     val kcEndWeek: Long,
     val kcIsDouble: Long,
     val kcIsSingle: Long,
     val kcWeekend: Long,
     val kcYear: Long,
     val kcXuenian: Long,
     val kcNote: String,
     val kcBackgroundId: Long,
     val shoukeJihua: String,
     val jiaoxueDagang: String,
     val teacher: String,
     val priority: Long,
     val type: Long,
)

fun CourseBean.toCourseBeanForShow():CourseBeanForShow{
    return CourseBeanForShow(
        courseId = this.courseId,
        kcName = this.kcName,
        kcLocation = this.kcLocation,
        kcStartTime = this.kcStartTime,
        kcEndTime = this.kcEndTime,
        kcStartWeek = this.kcStartWeek,
        kcEndWeek = this.kcEndWeek,
        kcIsDouble = this.kcIsDouble,
        kcIsSingle = this.kcIsSingle,
        kcWeekend = this.kcWeekend,
        kcYear = this.kcYear,
        kcXuenian = this.kcXuenian,
        kcNote = this.kcNote,
        kcBackgroundId = this.kcBackgroundId,
        shoukeJihua = this.shoukeJihua,
        jiaoxueDagang = this.jiaoxueDagang,
        teacher = this.teacher,
        priority = this.priority,
        type = this.type,
    )
}

