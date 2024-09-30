package ui.compose.emptyRoom

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
  )
  @Composable
  override fun Content() {
    val emptyRoomVoyagerViewModel = koinInject<EmptyRoomVoyagerViewModel>()
    val startClass = remember { mutableStateOf(1) }
    val endClass = remember { mutableStateOf(1) }
    val selectDateExpand = remember { mutableStateOf(false) }
    val selectCampus = remember { mutableStateOf("旗山校区") }
    val emptyData = emptyRoomVoyagerViewModel.availableEmptyRoomData.collectAsState()
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
          override fun isSelectableDate(utcTimeMillis: Long): Boolean {
            val day = fromEpochMilliseconds(utcTimeMillis).toLocalDateTime(CurrentZone).dayOfYear
            val now = Clock.System.now().toLocalDateTime(CurrentZone).dayOfYear
            return day >= now && day < now + 30
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
            .padding(horizontal = 10.dp),
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
                    campusList.forEach {
                      DropdownMenuItem(
                        onClick = {
                          selectCampus.value = it
                          expanded.value = false
                        },
                      ) {
                        Text(it)
                      }
                    }
                  },
                )
              }
            }
            emptyData.CollectWithContentInBox(
              success = {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                  it.forEach {
                    item {
                      Row(
                        modifier =
                        Modifier.wrapContentHeight().fillParentMaxWidth(1f).padding(2.dp),
                      ) {
                        Text(it.location, modifier = Modifier.weight(1f))
                        Text(it.type, modifier = Modifier.weight(1f))
                        Text(it.capacity, modifier = Modifier.weight(1f))
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
              val dateForSend = selectDate.value
              dateForSend
                ?: run {
                  return@FloatingActionButton
                }
              emptyRoomVoyagerViewModel.getAvailableEmptyRoomData(
                campus = selectCampus.value,
                date = dateForSend,
                start = startClass.value.toString(),
                end = endClass.value.toString(),
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
                        else MaterialTheme.colors.primary,
                      )
                        .value,
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
                        else MaterialTheme.colors.primary,
                      )
                        .value,
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

/** 各个校区 */
var campusList =
  arrayListOf(
    "旗山校区",
    "晋江校区",
    "铜盘校区",
    "怡山校区",
    "泉港校区",
    "厦门工艺美院",
  )
