package ui.compose.Exam

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import config.CurrentZone
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Transient
import org.koin.compose.koinInject
import ui.compose.ClassSchedule.AcademicYearSelectsDialog
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.compose.toastBindNetworkResult
import util.math.parseInt
import util.network.CollectWithContentInBox

class ExamVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {

  @Composable
  override fun Content() {

    val examVoyagerScreenViewModel = koinInject<ExamVoyagerScreenViewModel>()
    val toastState = rememberToastState()
    toastState.toastBindNetworkResult(examVoyagerScreenViewModel.refreshExamState.collectAsState())

    val academicYearSelectsDialogState = remember { mutableStateOf(false) }
    val yearOptionsBean = examVoyagerScreenViewModel.yearOptions.collectAsState(listOf())
    val examToCourse = examVoyagerScreenViewModel.examToCourse.collectAsState()

    Scaffold{
      Box(
        modifier =
        Modifier.fillMaxSize()
          .parentSystemControl(parentPaddingControl)
          .padding(horizontal = 10.dp),
      ) {
        val examList = examVoyagerScreenViewModel.examList.collectAsState(listOf())
        Column(modifier = Modifier.padding(10.dp)) {
          Row(modifier = Modifier.height(64.dp), verticalAlignment = Alignment.CenterVertically) {
            Row(modifier = Modifier.weight(1f)) {
              val weekExpanded = remember { mutableStateOf(false) }
              TextButton(onClick = { weekExpanded.value = true }) { Text(text = "考试列表") }
            }

            IconButton(onClick = { examVoyagerScreenViewModel.refreshExamData() }) {
              examVoyagerScreenViewModel.refreshExamState
                .collectAsState()
                .CollectWithContentInBox(
                  loading = {
                    CircularProgressIndicator(
                      modifier =
                      Modifier.fillMaxHeight()
                        .aspectRatio(1f)
                        .wrapContentSize(Alignment.Center)
                        .fillMaxSize(0.8f)
                    )
                  },
                  content = {
                    Icon(
                      imageVector = Icons.Filled.Refresh,
                      contentDescription = null,
                      modifier = Modifier.align(Alignment.Center),
                    )
                  },
                )
            }

            val expanded = remember { mutableStateOf(false) }
            Surface(modifier = Modifier.wrapContentSize()) {
              IconButton(onClick = { expanded.value = true }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Localized description")
              }
              DropdownMenu(
                expanded = expanded.value,
                onDismissRequest = { expanded.value = false }) {
                DropdownMenuItem(
                  text = { Text(text = "学年") },
                  onClick = {
                    expanded.value = false
                    academicYearSelectsDialogState.value = true
                  },
                  leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) },
                  trailingIcon = {
                    val isCurrent =
                      remember(examVoyagerScreenViewModel.selectYear.collectAsState().value) {
                        derivedStateOf { examVoyagerScreenViewModel.isCurrentYear() }
                      }
                    Text(
                      "${examVoyagerScreenViewModel.selectYear.collectAsState().value}${if (isCurrent.value) "(当前)" else ""}",
                      textAlign = TextAlign.Center,
                    )
                  },
                )
                DropdownMenuItem(
                  text = { Text("导出到课程表") },
                  onClick = { },
                  leadingIcon = {
                    Checkbox(
                    checked = (examToCourse.value != null && examToCourse.value == 1),
                    onCheckedChange = {
                      examVoyagerScreenViewModel.switchExamToCourse()
                    },
                  )},
                )
                DropdownMenuItem(
                  text = { Text("导出到日历") },
                  onClick = { },
                  leadingIcon = { },
                )
              }
            }
          }
          LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(examList.value.filter { it.exam.address.isNotEmpty() }) {
              Card {
                Column(
                  modifier = Modifier.fillMaxWidth().padding(10.dp),
                  verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                  with(it) {
                    val timeDatePeriod = remember { mutableStateOf("解析中...") }
                    val color = remember { mutableStateOf(Color.Gray) }
                    LaunchedEffect(Unit) {
                      timeDatePeriod.value = "解析中..."
                      color.value = Color.Gray
                      try {
                        val datePattern =
                          Regex(
                            """(\d{4})年(\d{2})月(\d{2})日 (\d{2}):(\d{2})-(\d{2}):(\d{2}) (\S+)"""
                          )
                            .matchEntire(exam.address)
                        datePattern
                          ?: run {
                            timeDatePeriod.value = "解析失败"
                            color.value = Color.Red
                            return@LaunchedEffect
                          }
                        datePattern.groupValues.let {
                          val dateTime =
                            LocalDateTime(
                              year = parseInt(it[1]),
                              month = Month(parseInt(it[2])),
                              dayOfMonth = parseInt(it[3]),
                              hour = 12,
                              minute = 12,
                              second = 12,
                            )
                              .dayOfYear
                          val currentDate =
                            Clock.System.now().toLocalDateTime(CurrentZone).dayOfYear
                          if (dateTime - currentDate < 0) {
                            timeDatePeriod.value = "已经结束"
                            color.value = Color.Cyan
                          } else if (dateTime - currentDate == 0) {
                            timeDatePeriod.value = "就在今天"
                            color.value = Color.Red
                          } else {
                            timeDatePeriod.value = "还有 ${dateTime - currentDate} 天"
                            when {
                              dateTime - currentDate > 14 -> {
                                color.value = Color.Green
                              }
                              dateTime - currentDate > 7 && dateTime - currentDate > 3 -> {
                                color.value = Color.Yellow
                              }
                              dateTime - currentDate < 3 -> {
                                color.value = Color.Red
                              }
                            }
                          }
                        }
                      } catch (e: Exception) {
                        timeDatePeriod.value = "解析失败"
                        return@LaunchedEffect
                      }
                    }
                    Text(exam.name, fontSize = 20.sp)
                    Text("学分: ${exam.xuefen}")
                    Text("老师: ${exam.teacher}")
                    Text("地址: ${exam.address}")
                    Text("时间期限: ${timeDatePeriod.value}", color = color.value)
                  }
                }
              }
            }
          }
        }
        if (academicYearSelectsDialogState.value) {
          val currentYear = examVoyagerScreenViewModel.currentYear.collectAsState()
          AcademicYearSelectsDialog(
            onDismissRequest = { academicYearSelectsDialogState.value = false },
            commit = { examVoyagerScreenViewModel.changeCurrentYear(it) },
            list = yearOptionsBean.value,
            currentYear = currentYear,
          )
        }
        EasyToast(toastState)
      }
    }
  }
}

