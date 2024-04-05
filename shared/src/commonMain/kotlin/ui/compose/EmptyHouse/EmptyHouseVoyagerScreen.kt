package ui.compose.EmptyHouse

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
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.todayIn
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl

class EmptyHouseVoyagerScreen(
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) :Screen{
    @OptIn(ExperimentalMaterial3Api::class, FormatStringsInDatetimeFormats::class,
        ExperimentalLayoutApi::class
    )
    @Composable
    override fun Content() {
        val startClass = remember {
            mutableStateOf<Int>(1)
        }
        val endClass = remember {
            mutableStateOf<Int>(1)
        }
        LaunchedEffect(startClass.value){
            if (startClass.value > endClass.value){
                endClass.value = startClass.value
            }
        }
        val selectDate = remember {
            mutableStateOf(false)
        }
        val selectCampus = remember {
            mutableStateOf(campusList.first())
        }

        val scaffoldState = rememberScaffoldState()
        val date = rememberDatePickerState(
            initialSelectedDateMillis = Clock.System.todayIn(TimeZone.UTC).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        )
        val scope = rememberCoroutineScope()
        Scaffold(
            modifier = Modifier,
            content = {
                  Box(
                      modifier = Modifier
                          .fillMaxSize()
                          .parentSystemControl(parentPaddingControl)
                          .padding(horizontal = 10.dp)
                  ){
                      Column (
                          modifier = Modifier
                              .fillMaxSize()
                      ){
                          FlowRow(
                              horizontalArrangement = Arrangement.spacedBy(20.dp)
                          ) {
                              Button(
                                  onClick = {
                                      scope.launch {
                                          scaffoldState.drawerState.open()
                                      }
                                  },
                                  content = {
                                      Text(
                                          "第 ${startClass.value} 节课 -> 第 ${endClass.value} 节课",
                                          modifier = Modifier,
                                          textAlign = TextAlign.Center
                                      )
                                  }
                              )
                              Button(
                                  onClick = {
                                      selectDate.value = true
                                  }
                              ){
                                  Text(date.selectedDateMillis?.let { it1 ->
                                      val dateTime = Instant.fromEpochMilliseconds(
                                          it1
                                      ).toLocalDateTime(TimeZone.UTC)

                                      return@let dateTime.toInstant(TimeZone.UTC)
                                          .toLocalDateTime(TimeZone.UTC).date.format(LocalDate.Format {
                                              byUnicodePattern(
                                                  "yyyy-MM-dd"
                                              )
                                          })
                                  }.toString())
                              }
                              Box {
                                  val expanded = remember {
                                      mutableStateOf(false)
                                  }
                                  Button(
                                      onClick = {
                                          scope.launch {
                                             expanded.value = true
                                          }
                                      },
                                      content = {
                                          Text(
                                              selectCampus.value
                                          )
                                      }
                                  )
                                  DropdownMenu(
                                      expanded = expanded.value,
                                      onDismissRequest = {
                                          expanded.value = false
                                      },
                                      content = {
                                          campusList.forEach {
                                              DropdownMenuItem(
                                                  onClick = {
                                                      selectCampus.value = it
                                                      expanded.value = false
                                                  }
                                              ){
                                                  Text(it)
                                              }
                                          }
                                      }
                                  )
                              }
                          }
                      }
                      if (selectDate.value){
                          DatePickerDialog(
                              onDismissRequest = {
                                  selectDate.value = false
                              },
                              confirmButton = {
                                  TextButton(
                                      onClick = {
                                          selectDate.value = false
                                      },
                                  ) {
                                      Text("确定")
                                  }
                              }
                          ){
                              DatePicker(
                                  state = date,
                                  title = {
                                      Text("选择日期", modifier = Modifier.padding(start = 20.dp))
                                  },
                                  dateValidator = {
                                      val currentDate: LocalDate = Clock.System.todayIn(TimeZone.UTC)
                                      val zeroTime = currentDate.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
                                      return@DatePicker zeroTime <= it
                                  },
                                  showModeToggle = false
                              )
                          }
                      }
                      FloatingActionButton(
                          onClick = {

                          },
                          modifier = Modifier
                              .align(Alignment.BottomEnd)
                              .offset((-10).dp, (-10).dp)
                      ){
                          Icon(
                              imageVector = Icons.Filled.Refresh,
                              contentDescription = null
                          )
                      }
                  }
            },
            drawerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .parentSystemControl(parentPaddingControl)
                ) {
                    Surface (
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .padding(10.dp)
                    ){
                        Text(
                            "第 ${startClass.value} 节课 -> 第 ${endClass.value} 节课",
                            modifier = Modifier
                                .fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                    ){
                        LazyColumn (
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            (1..11).forEach {
                                item {
                                    Button(
                                        onClick = {
                                            startClass.value = it
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = animateColorAsState(
                                                if (it == startClass.value) Color(51, 201, 199,100) else MaterialTheme.colors.primary
                                            ).value
                                        )
                                    ){
                                        Text(it.toString())
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                                .wrapContentSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text("到")
                        }
                        LazyColumn (
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
                            (1..11).forEach {
                                item {
                                    Button(
                                        onClick = {
                                            endClass.value = it
                                        },
                                        enabled = (it >= startClass.value),
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = animateColorAsState(
                                                if (it == endClass.value) Color(51, 201, 199,170) else MaterialTheme.colors.primary
                                            ).value
                                        )
                                    ){
                                        Text(it.toString())
                                    }
                                }
                            }
                        }
                    }
                }
            },
            scaffoldState = scaffoldState,
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen
        )
    }
}





val campusList = listOf(
    "旗山校区",
    "泉港校区",
    "铜盘校区",
    "厦门工艺美院",
    "怡山校区",
)