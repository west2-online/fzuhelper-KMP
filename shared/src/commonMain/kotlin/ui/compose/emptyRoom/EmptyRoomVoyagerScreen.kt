package ui.compose.emptyRoom

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import config.CurrentZone
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.Instant.Companion.fromEpochMilliseconds
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.network.CollectWithContentInBox
import util.network.logicWithTypeWithLimit
import kotlin.jvm.Transient

class BuildForSelect(val isSelect: MutableState<Boolean>, val name: String)

/**
 * 空教室一级屏幕
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class EmptyRoomVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @OptIn(
    ExperimentalMaterial3Api::class,
    FormatStringsInDatetimeFormats::class,
    ExperimentalLayoutApi::class,
    ExperimentalFoundationApi::class,
  )
  @Composable
  override fun Content() {
    val emptyRoomVoyagerViewModel = koinInject<EmptyRoomVoyagerViewModel>()
    val startClass = remember { mutableStateOf<Int>(1) }
    val endClass = remember { mutableStateOf<Int>(1) }
    val selectDateExpand = remember { mutableStateOf(false) }
    val selectCampus = remember { mutableStateOf("旗山校区") }
    val buildForSelect = remember {
      derivedStateOf<List<BuildForSelect>?> {
        campusList[selectCampus.value]?.map {
          BuildForSelect(isSelect = mutableStateOf(it.second), name = it.first)
        }
      }
    }
    val emptyData = emptyRoomVoyagerViewModel.availableEmptyRoomData.collectAsState()
    val selectBuild = remember { mutableStateOf<String?>("旗山校区") }
    val toastState = rememberToastState()

    emptyData.value.logicWithTypeWithLimit(
      error = { toastState.addWarnToast(it.message.toString()) },
      success = { toastState.addToast("获取成功") },
    )
    LaunchedEffect(startClass.value) {
      if (startClass.value > endClass.value) {
        endClass.value = startClass.value
      }
    }

    val scaffoldState = rememberScaffoldState()
    val date =
      rememberDatePickerState(
        initialSelectedDateMillis =
          Clock.System.todayIn(TimeZone.UTC).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds(),
        selectableDates =
          object : SelectableDates {
            // Blocks Sunday and Saturday from being selected.
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
              return fromEpochMilliseconds(utcTimeMillis).toLocalDateTime(CurrentZone).dayOfYear >=
                Clock.System.now().toLocalDateTime(CurrentZone).dayOfYear &&
                fromEpochMilliseconds(utcTimeMillis).toLocalDateTime(CurrentZone).dayOfYear <
                  Clock.System.now().toLocalDateTime(CurrentZone).dayOfYear + 5
            }

            // Allow selecting dates from year 2023 forward.
            override fun isSelectableYear(year: Int): Boolean {
              return year == Clock.System.now().toLocalDateTime(CurrentZone).year
            }
          },
      )
    val selectDate = remember {
      derivedStateOf {
        date.selectedDateMillis?.let { it1 ->
          val dateTime = Instant.fromEpochMilliseconds(it1).toLocalDateTime(TimeZone.UTC)

          return@let dateTime
            .toInstant(TimeZone.UTC)
            .toLocalDateTime(TimeZone.UTC)
            .date
            .format(LocalDate.Format { byUnicodePattern("yyyy-MM-dd") })
        }
      }
    }
    val scope = rememberCoroutineScope()
    Scaffold(
      modifier = Modifier,
      content = {
        Box(
          modifier =
            Modifier.fillMaxSize()
              .parentSystemControl(parentPaddingControl)
              .padding(horizontal = 10.dp)
        ) {
          Column(modifier = Modifier.fillMaxSize()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
              Button(
                onClick = { scope.launch { scaffoldState.drawerState.open() } },
                content = {
                  Text(
                    "第 ${startClass.value} 节课 -> 第 ${endClass.value} 节课",
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                  )
                },
              )
              Button(onClick = { selectDateExpand.value = true }) {
                Text(selectDate.value.toString())
              }
              Box {
                val expanded = remember { mutableStateOf(false) }
                Button(
                  onClick = { scope.launch { expanded.value = true } },
                  content = { Text(selectCampus.value) },
                )
                DropdownMenu(
                  expanded = expanded.value,
                  onDismissRequest = { expanded.value = false },
                  content = {
                    campusList.keys.forEach {
                      DropdownMenuItem(
                        onClick = {
                          selectCampus.value = it
                          expanded.value = false
                        }
                      ) {
                        Text(it)
                      }
                    }
                  },
                )
              }
              Box {
                val expanded = remember { mutableStateOf(false) }
                Button(
                  onClick = { scope.launch { expanded.value = true } },
                  content = { Text("选择教学楼") },
                )
                DropdownMenu(
                  expanded = expanded.value,
                  onDismissRequest = { expanded.value = false },
                  content = {
                    buildForSelect.value?.forEach { buildForSelect ->
                      DropdownMenuItem(
                        onClick = { buildForSelect.isSelect.value = !buildForSelect.isSelect.value }
                      ) {
                        Checkbox(
                          checked = buildForSelect.isSelect.value,
                          onCheckedChange = {
                            buildForSelect.isSelect.value = !buildForSelect.isSelect.value
                          },
                        )
                        Text(buildForSelect.name)
                      }
                    }
                  },
                )
              }
            }
            emptyData.CollectWithContentInBox(
              success = {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                  it?.forEach {
                    stickyHeader { TopAppBar { Text(it.key) } }
                    it.value?.forEach {
                      item {
                        Row(
                          modifier =
                            Modifier.wrapContentHeight().fillParentMaxWidth(1f).padding(2.dp)
                        ) {
                          Text(it.Name, modifier = Modifier.weight(1f))
                          Text(it.RoomType, modifier = Modifier.weight(1f))
                          Text(it.Number, modifier = Modifier.weight(1f))
                        }
                      }
                    }
                  }
                }
              },
              loading = { CircularProgressIndicator(modifier = Modifier.align(Alignment.Center)) },
              modifier = Modifier.weight(1f).fillMaxWidth(),
            )
          }
          if (selectDateExpand.value) {
            DatePickerDialog(
              onDismissRequest = { selectDateExpand.value = false },
              confirmButton = {
                TextButton(onClick = { selectDateExpand.value = false }) { Text("确定") }
              },
            ) {
              DatePicker(
                state = date,
                title = { Text("选择日期", modifier = Modifier.padding(start = 20.dp)) },
                //                                  dateValidator = {
                //                                      val currentDate: LocalDate =
                // Clock.System.todayIn(TimeZone.UTC)
                //                                      val zeroTime =
                // currentDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                //                                      return@DatePicker zeroTime <= it
                //                                  },
                showModeToggle = false,
              )
            }
          }
          FloatingActionButton(
            onClick = {
              val buildForSend = buildForSelect.value
              buildForSend
                ?: run {
                  return@FloatingActionButton
                }
              val dateForSend = selectDate.value
              dateForSend
                ?: run {
                  return@FloatingActionButton
                }
              emptyRoomVoyagerViewModel.getAvailableEmptyRoomData(
                campus = selectCampus.value,
                date = dateForSend,
                roomType = "普通教室",
                start = startClass.value.toString(),
                end = endClass.value.toString(),
                build = buildForSend.filter { it.isSelect.value }.map { it.name },
              )
            },
            modifier = Modifier.align(Alignment.BottomEnd).offset((-10).dp, (-10).dp),
          ) {
            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
          }
          EasyToast(toastState)
        }
      },
      drawerContent = {
        Column(modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl)) {
          Surface(modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(10.dp)) {
            Text(
              "第 ${startClass.value} 节课 -> 第 ${endClass.value} 节课",
              modifier = Modifier.fillMaxWidth(),
              textAlign = TextAlign.Center,
            )
          }
          Row(modifier = Modifier.fillMaxWidth()) {
            LazyColumn(
              verticalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              (1..11).forEach {
                item {
                  Button(
                    onClick = { startClass.value = it },
                    colors =
                      ButtonDefaults.buttonColors(
                        backgroundColor =
                          animateColorAsState(
                              if (it == startClass.value) Color(51, 201, 199, 100)
                              else MaterialTheme.colors.primary
                            )
                            .value
                      ),
                  ) {
                    Text(it.toString())
                  }
                }
              }
            }
            Column(
              modifier = Modifier.fillMaxHeight().wrapContentSize(),
              verticalArrangement = Arrangement.Center,
            ) {
              Text("到")
            }
            LazyColumn(
              verticalArrangement = Arrangement.spacedBy(10.dp),
              modifier = Modifier.weight(1f),
              horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              (1..11).forEach {
                item {
                  Button(
                    onClick = { endClass.value = it },
                    enabled = (it >= startClass.value),
                    colors =
                      ButtonDefaults.buttonColors(
                        backgroundColor =
                          animateColorAsState(
                              if (it == endClass.value) Color(51, 201, 199, 170)
                              else MaterialTheme.colors.primary
                            )
                            .value
                      ),
                  ) {
                    Text(it.toString())
                  }
                }
              }
            }
          }
        }
      },
      scaffoldState = scaffoldState,
      drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
    )
  }
}

/** 各个校区及其对应的建筑 */
var campusList =
  mapOf(
    "旗山校区" to
      listOf(
        Pair("第1学科群", false),
        Pair("第2学科群", false),
        Pair("第3学科群", false),
        Pair("第4学科群", false),
        Pair("第5学科群", false),
        Pair("公共教学楼东1", true),
        Pair("公共教学楼东2", true),
        Pair("公共教学楼东3", true),
        Pair("公共教学楼文科楼", false),
        Pair("公共教学楼西1", true),
        Pair("公共教学楼西2", true),
        Pair("公共教学楼西3", true),
        Pair("公共教学楼中楼", true),
        Pair("国家科技园", false),
        Pair("晋江楼", false),
        Pair("素拓中心", false),
        Pair("田径场", false),
        Pair("图书馆", false),
        Pair("音乐实训基地", false),
        Pair("紫金教学楼", false),
      ),
    "晋江校区" to listOf(Pair("A区", true), Pair("B区中庭", true), Pair("田径场", true)),
    "铜盘校区" to listOf(Pair("A楼", true), Pair("B楼", true), Pair("铜盘田径场", true)),
    "怡山校区" to
      listOf(
        Pair("北楼", true),
        Pair("地矿楼", true),
        Pair("电教", true),
        Pair("电气楼", true),
        Pair("东教", true),
        Pair("管理学院", true),
        Pair("机械楼", true),
        Pair("计算机楼", true),
        Pair("轻工楼", true),
        Pair("田径场", true),
        Pair("图书馆", true),
        Pair("土建楼", true),
        Pair("土乙楼", true),
        Pair("文科楼", true),
        Pair("物理楼", true),
        Pair("西教", true),
        Pair("至诚学院", true),
        Pair("子兴楼", true),
      ),
    "泉港校区" to listOf(Pair("泉港教学楼", true), Pair("泉港实验一号楼", true)),
    "厦门工艺美院" to listOf(Pair("鼓浪屿校区", true), Pair("集美校区", true)),
  )
