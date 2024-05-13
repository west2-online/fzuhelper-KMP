package ui.compose.ClassSchedule


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.futalk.kmm.CourseBean
import com.futalk.kmm.YearOptions
import config.CurrentZone
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.example.library.MR
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.ScrollSelection
import util.compose.defaultSelfPaddingControl
import util.compose.parentStatusControl
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.network.CollectWithContentInBox
import kotlin.jvm.Transient
import kotlin.time.DurationUnit
import kotlin.time.toDuration


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class,
    ExperimentalResourceApi::class
)
@Composable
fun ClassSchedule(
    classScheduleViewModel: ClassScheduleViewModel = koinInject<ClassScheduleViewModel>(),
    parentPaddingControl: ParentPaddingControl,
){
    val showExamList = rememberSaveable {
        mutableStateOf(false)
    }
    val toastState = rememberToastState()
    toastState.toastBindNetworkResult(classScheduleViewModel.refreshState.collectAsState())
    toastState.toastBindNetworkResult(classScheduleViewModel.refreshExamState.collectAsState())
    val pageNumber = remember {
        mutableStateOf(30)
    }
    val pagerState = rememberPagerState(
        initialPage = classScheduleViewModel.selectWeek.value - 1,
        initialPageOffsetFraction = 0f
    ) {
        pageNumber.value
    }
    val courseDialog by classScheduleViewModel.courseDialog.collectAsState()
    val academicYearSelectsDialogState = remember {
        mutableStateOf(false)
    }
    val yearOptionsBean by classScheduleViewModel.yearOptions.collectAsState(listOf())
    val currentWeek by classScheduleViewModel.selectWeek.collectAsState()
    LaunchedEffect(currentWeek){
        pagerState.animateScrollToPage(classScheduleViewModel.selectWeek.value - 1)
    }
    DisposableEffect(Unit){
        onDispose {
            classScheduleViewModel.selectWeek.value = pagerState.currentPage + 1
        }
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ){
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .parentStatusControl(parentPaddingControl = parentPaddingControl),
            ) {
                Row (
                    modifier = Modifier.height(64.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ){
                    Row (
                        modifier = Modifier
                            .weight(1f)
                    ){
                        val weekExpanded = remember {
                            mutableStateOf(false)
                        }

                        TextButton(
                            onClick = {
                                weekExpanded.value = true
                            }
                        ){
                            val isCurrent = remember(pagerState.currentPage,classScheduleViewModel.selectYear.collectAsState().value) {
                                derivedStateOf {
                                    classScheduleViewModel.isCurrentWeek(pagerState.currentPage + 1)
                                }
                            }
                            Text(
                                text = "  第${pagerState.currentPage + 1}周${if(isCurrent.value) "(当前)" else ""}",
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                modifier = Modifier
                                    .rotate(
                                        animateFloatAsState(
                                            if( weekExpanded.value ) 0f else 180f,
                                            animationSpec = tween(500)
                                        ).value
                                    )
                            )
                        }
                        Box{
                            DropdownMenu(
                                expanded = weekExpanded.value,
                                onDismissRequest = {
                                    weekExpanded.value = false
                                }
                            ){
                                (1..30).forEach {
                                    val isCurrent = remember(pagerState.currentPage,classScheduleViewModel.selectYear.collectAsState().value) {
                                        derivedStateOf {
                                            classScheduleViewModel.isCurrentWeek(it)
                                        }
                                    }
                                    DropdownMenuItem(
                                        onClick = {
                                            classScheduleViewModel.selectWeek.value = it
                                            weekExpanded.value = false
                                        },
                                        text = {
                                            Text("第${it}周${if(isCurrent.value) "(当前)" else ""}")
                                        }
                                    )
                                }

                            }
                        }
                    }

                    IconButton(onClick = {
                        showExamList.value = true
                    }) {
                        Icon(
                            painter = painterResource(MR.images.exam).apply {
                                    this.intrinsicSize
                            },
                            null,
                            modifier = Modifier
                                .fillMaxHeight()
                                .aspectRatio(1f)
                                .wrapContentSize(Alignment.Center)
                                .fillMaxSize(0.55f)
                        )
                    }
                    IconButton(onClick = {
                        classScheduleViewModel.refreshClassData()
                    }) {
                        classScheduleViewModel.refreshState.collectAsState().CollectWithContentInBox(
                            loading = {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .aspectRatio(1f)
                                        .wrapContentSize(Alignment.Center)
                                        .fillMaxSize(0.8f)
                                )
                            },
                            content = {
                                Icon(
                                    imageVector = Icons.Filled.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .align(Alignment.Center)
                                )
                            }
                        )
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
                                    academicYearSelectsDialogState.value = true
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Outlined.Edit,
                                        contentDescription = null
                                    )
                                },
                                trailingIcon = {
                                    val isCurrent = remember(classScheduleViewModel.selectYear.collectAsState().value) {
                                        derivedStateOf {
                                            classScheduleViewModel.isCurrentYear()
                                        }
                                    }
                                    Text(
                                        "${ classScheduleViewModel.selectYear.collectAsState().value }${if(isCurrent.value) "(当前)" else ""}",
                                        textAlign = TextAlign.Center
                                    )
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
            }
            TimeOfWeekColumn(
                week = pagerState.currentPage + 1,
                startMonthState = classScheduleViewModel.classScheduleUiState.startMonth.collectAsState(
                    1
                ),
                startYearState = classScheduleViewModel.classScheduleUiState.startYear.collectAsState(
                    2023
                ),
                startDayState = classScheduleViewModel.classScheduleUiState.startDay.collectAsState(
                    1
                )
            )
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                Surface(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .wrapContentWidth()
                        .fillMaxHeight()
                ) {
                    Sidebar(
                        classScheduleViewModel.scrollState
                    )
                }
                HorizontalPager(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    state = pagerState,
                ) { page ->
                    Column {
                        TimeOfMonthColumn(
                            week = pagerState.currentPage + 1,
                            startMonthState = classScheduleViewModel.classScheduleUiState.startMonth.collectAsState(
                                1
                            ),
                            startYearState = classScheduleViewModel.classScheduleUiState.startYear.collectAsState(
                                2023
                            ),
                            startDayState = classScheduleViewModel.classScheduleUiState.startDay.collectAsState(
                                1
                            )
                        )
                        Row(
                            Modifier
                                .fillMaxSize()
                                .verticalScroll(classScheduleViewModel.scrollState)
                        ) {
                            WeekDay.entries.forEachIndexed { weekIndex, value ->
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .requiredHeight((11 * 75).dp)
                                ) {
                                    classScheduleViewModel.courseForShow.collectAsState().value.let { courseBeans ->
                                        courseBeans
                                            .filter {
                                                it.kcStartWeek <= page + 1 && it.kcEndWeek >= page + 1 && (it.kcIsDouble.toInt() == 1 && ((page + 1) % 2 == 0) || it.kcIsSingle.toInt() == 1 && ((page + 1) % 2 == 1))
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
                                                    if (index == 0) {
                                                        ClassCard(
                                                            item,
                                                            detailAboutCourse = {
                                                                classScheduleViewModel.courseDialog.value =
                                                                    it
                                                            }
                                                        )
                                                    } else if (it[index].kcStartTime > it[index - 1].kcEndTime) {
                                                        ClassCard(
                                                            item,
                                                            detailAboutCourse = {
                                                                classScheduleViewModel.courseDialog.value =
                                                                    it
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
        if (academicYearSelectsDialogState.value) {
            val currentYear = classScheduleViewModel.currentYear.collectAsState()
            AcademicYearSelectsDialog(
                onDismissRequest = {
                    academicYearSelectsDialogState.value = false
                },
                commit = {
                    classScheduleViewModel.changeCurrentYear(it)
                },
                list = yearOptionsBean,
                currentYear = currentYear
            )
        }
        AnimatedVisibility(
            showExamList.value,
            enter = slideInVertically { fullHeight -> fullHeight  },
            exit = slideOutVertically { fullHeight -> fullHeight  },
            modifier = Modifier
                .fillMaxSize()
                .parentStatusControl(parentPaddingControl)
        ) {
            Surface (
                modifier = Modifier
                    .fillMaxSize()
            ){
                val examList = classScheduleViewModel.examList.collectAsState(listOf())
                Column (
                    modifier = Modifier
                        .padding(10.dp)
                ){
                    Row (
                        modifier = Modifier.height(64.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ){
                        Row (
                            modifier = Modifier
                                .weight(1f)
                        ){
                            val weekExpanded = remember {
                                mutableStateOf(false)
                            }
                            TextButton(
                                onClick = {
                                    weekExpanded.value = true
                                }
                            ){
                                Text(
                                    text = "考试列表",
                                )
                            }
                        }

                        IconButton(onClick = {
                            showExamList.value = false
                        }) {
                            Icon(Icons.Default.KeyboardArrowDown,null)
                        }
                        IconButton(onClick = {
                            classScheduleViewModel.refreshExamData()
                        }) {
                            classScheduleViewModel.refreshExamState.collectAsState().CollectWithContentInBox(
                                loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .aspectRatio(1f)
                                            .wrapContentSize(Alignment.Center)
                                            .fillMaxSize(0.8f)
                                    )
                                },
                                content = {
                                    Icon(
                                        imageVector = Icons.Filled.Refresh,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                    )
                                }
                            )
                        }
                    }
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        items(examList.value.filter {
                            it.address.isNotEmpty()
                        }){
                            Card {
                                Column (
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(5.dp)
                                ){
                                    with(it){
                                        Text(
                                            name,
                                            fontSize = 20.sp
                                        )
                                        Text("学分: $xuefen")
                                        Text("老师: $teacher")
                                        Text("地址: $address")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        EasyToast(toastState)
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
            .background(
                (if(isSystemInDarkTheme()) DarkColors.entries[courseBean.kcBackgroundId.toInt()].color else LightColors.entries[courseBean.kcBackgroundId.toInt()].color)
            )
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun ClassDialog(
    courseBean: CourseBean,
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
                    title = {  },
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
            text = "${
                getMonthByWeek(
                    week = week,
                    startYear = startYear,
                    startMonth = startMonth,
                    startDay = startDay
                )
            }",
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
        )
        WeekDay.entries.forEach { item ->
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        WeekDay.entries.forEachIndexed{ index, _ ->
            val date = getDataByWeek(
                week = week,
                day = index,
                startYear = startYearState.value,
                startMonth = startMonthState.value,
                startDay = startDayState.value
            )
            Column (
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ){
                Text(
                    text = date.first.toString(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center
                )
                Spacer(
                    modifier = Modifier.fillMaxWidth(1f)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                        .fillMaxWidth(0.6f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(100))
                        .background(
                            animateColorAsState(if (date.second) Color.Blue else Color.Transparent).value
                        )
                )
            }
        }
    }
}

fun getDataByWeek(week: Int, day: Int, startYear: Int, startMonth: Int, startDay: Int, ): Pair<Int,Boolean>{
    //创建一个自定义年月日的日期，使用kotlin datetime
    return try {
        val time = LocalDateTime(startYear, startMonth, startDay, 16, 57, 0, 0).toInstant(CurrentZone)
            .plus(((week - 1) * 7).toDuration(DurationUnit.DAYS))
            .plus(day.toDuration(DurationUnit.DAYS))
        val currentDay = Clock.System.now().toLocalDateTime(CurrentZone)
        val parseDay = time.toLocalDateTime(CurrentZone)
        Pair(parseDay.dayOfMonth,(currentDay.year == parseDay.year && currentDay.dayOfYear == parseDay.dayOfYear))
    }catch (e:Exception){
        Pair(1,false)
    }
}


fun getMonthByWeek(week: Int,startYear:Int, startMonth:Int,startDay:Int):Int{
    //创建一个自定义年月日的日期，使用Calendar.set
    return try {
        val time = LocalDateTime(startYear, startMonth, startDay, 12, 0, 0, 1).toInstant(CurrentZone)
            .plus(((week - 1) * 7).toDuration(DurationUnit.DAYS))
        time.toLocalDateTime(CurrentZone).monthNumber
    }catch (e:Exception){
        1
    }
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
    currentYear :State<String>,
    commit: (String) -> Unit
){
    val data = MutableStateFlow(if(list.isNotEmpty()) list[0].yearOptionsName else "null")
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




class ClassScheduleVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
):Tab{
    @Composable
    override fun Content() {
        ClassSchedule(
            parentPaddingControl = parentPaddingControl
        )
    }

    override val options: TabOptions
        @Composable
        get(){
            return remember {
                TabOptions(
                    index = 0u,
                    title = ""
                )
            }
        }
}


enum class LightColors(val color: Color) {
    COLOR1(Color(0xFFECCDCD)),
    COLOR2(Color(0xFFDDEEFF)),
    COLOR3(Color(0xFFE6FFDD)),
    COLOR4(Color(0xFFFFEDDD)),
    COLOR5(Color(0xFFF9DDFF)),
    COLOR6(Color(0xFFFFDDDD)),
    COLOR7(Color(0xFFDDFFFB)),
    COLOR8(Color(0xFFFFFDDD)),
    COLOR9(Color(0xFFF2DDFF)),
    COLOR10(Color(0xFFDDFFDD)),
    COLOR11(Color(0xFFFFF7DD)),
    COLOR12(Color(0xFFDDFFFF)),
    COLOR13(Color(0xFFFCDDFF)),
    COLOR14(Color(0xFFFFFBDD)),
    COLOR15(Color(0xFFDDFFEF)),
    COLOR16(Color(0xFFE2DDFF)),
    COLOR17(Color(0xFFDDFFE3)),
    COLOR18(Color(0xFFFFDDF6)),
    COLOR19(Color(0xFFDDFFF7)),
    COLOR20(Color(0xFFF0FFDD))
}

enum class DarkColors(val color: Color) {
    COLOR1(Color(0xFFA56969)),  // 深色模式下的对应颜色1
    COLOR2(Color(0xFF7A9ACC)),  // 深色模式下的对应颜色2
    COLOR3(Color(0xFF7ACB9E)),  // 深色模式下的对应颜色3
    COLOR4(Color(0xFFCB8F7A)),  // 深色模式下的对应颜色4
    COLOR5(Color(0xFFC295D7)),  // 深色模式下的对应颜色5
    COLOR6(Color(0xFFD7A4A4)),  // 深色模式下的对应颜色6
    COLOR7(Color(0xFFA4D7C8)),  // 深色模式下的对应颜色7
    COLOR8(Color(0xFFD7D7A1)),  // 深色模式下的对应颜色8
    COLOR9(Color(0xFFBDA4D7)),  // 深色模式下的对应颜色9
    COLOR10(Color(0xFFA4D7A4)), // 深色模式下的对应颜色10
    COLOR11(Color(0xFFD7C2A4)), // 深色模式下的对应颜色11
    COLOR12(Color(0xFFA4D7D7)), // 深色模式下的对应颜色12
    COLOR13(Color(0xFFCDA4D7)), // 深色模式下的对应颜色13
    COLOR14(Color(0xFFD7C7A4)), // 深色模式下的对应颜色14
    COLOR15(Color(0xFFA4D7B6)), // 深色模式下的对应颜色15
    COLOR16(Color(0xFFB0A4D7)), // 深色模式下的对应颜色16
    COLOR17(Color(0xFFA4D7C1)), // 深色模式下的对应颜色17
    COLOR18(Color(0xFFD7A4CD)), // 深色模式下的对应颜色18
    COLOR19(Color(0xFFA4D7CE)), // 深色模式下的对应颜色19
    COLOR20(Color(0xFFC5D7A4))  // 深色模式下的对应颜色20
}
