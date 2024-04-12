package ui.compose.EmptyHouse

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material.TextField
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import asImageBitmap
import cafe.adriel.voyager.core.screen.Screen
import configureForPlatform
import data.emptyRoom.UnAvailable
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.statement.readBytes
import io.ktor.http.Cookie
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
import org.koin.compose.koinInject
import ui.compose.Test.CustomCookiesStorage
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.network.CollectWithContentInBox
import util.network.NetworkResult
import util.network.logicWithTypeWithLimit
import kotlin.jvm.Transient

class EmptyHouseVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) :Screen{
    @OptIn(ExperimentalMaterial3Api::class, FormatStringsInDatetimeFormats::class,
        ExperimentalLayoutApi::class, ExperimentalFoundationApi::class
    )
    @Composable
    override fun Content() {
        val emptyHouseVoyagerViewModel = koinInject<EmptyHouseVoyagerViewModel>()
        val startClass = remember {
            mutableStateOf<Int>(1)
        }
        val endClass = remember {
            mutableStateOf<Int>(1)
        }
        val selectDateExpand = remember {
            mutableStateOf(false)
        }
        val selectCampus = remember {
            mutableStateOf("旗山校区")
        }
        val buildForSelect = remember {
            derivedStateOf {
                campusList[selectCampus.value]
            }
        }
        val emptyData = emptyHouseVoyagerViewModel.availableEmptyRoomData.collectAsState()
        val selectBuild = remember {
            mutableStateOf<String?>("旗山校区")
        }
        val toastState = rememberToastState()
        val refresh = remember {
            mutableStateOf(false)
        }
        emptyData.value.logicWithTypeWithLimit(
            error = {
                if (it is UnAvailable){
                    refresh.value = true
                }
                else{
                    toastState.addWarnToast(it.message.toString())
                }
            }
        )
        LaunchedEffect(startClass.value){
            if (startClass.value > endClass.value){
                endClass.value = startClass.value
            }
        }
        LaunchedEffect(buildForSelect.value){
            selectBuild.value = buildForSelect.value?.get(0)
        }
        val scaffoldState = rememberScaffoldState()
        val date = rememberDatePickerState(
            initialSelectedDateMillis = Clock.System.todayIn(TimeZone.UTC).atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
        )
        val selectDate = remember {
            derivedStateOf {
                date.selectedDateMillis?.let { it1 ->
                    val dateTime = Instant.fromEpochMilliseconds(
                        it1
                    ).toLocalDateTime(TimeZone.UTC)

                    return@let dateTime.toInstant(TimeZone.UTC)
                        .toLocalDateTime(TimeZone.UTC).date.format(LocalDate.Format {
                            byUnicodePattern(
                                "yyyy-MM-dd"
                            )
                        })
                }
            }
        }
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
                                      selectDateExpand.value = true
                                  }
                              ){
                                  Text(selectDate.value.toString())
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
                                          campusList.keys.forEach {
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
                                              selectBuild.value?:"无"
                                          )
                                      }
                                  )
                                  DropdownMenu(
                                      expanded = expanded.value,
                                      onDismissRequest = {
                                          expanded.value = false
                                      },
                                      content = {
                                          buildForSelect.value?.forEach {
                                              DropdownMenuItem(
                                                  onClick = {
                                                      selectBuild.value = it
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
                          emptyData.CollectWithContentInBox(
                              success = {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                ) {
                                    it?.forEach {
                                        stickyHeader {
                                            TopAppBar {
                                                Text(it.key)
                                            }
                                        }
                                        it.value?.forEach {
                                            item {
                                                Row (
                                                    modifier = Modifier
                                                        .wrapContentHeight()
                                                        .fillParentMaxWidth(1f)
                                                        .padding(2.dp)
                                                ){
                                                    Text(it.Name, modifier = Modifier.weight(1f))
                                                    Text(it.RoomType,modifier = Modifier.weight(1f))
                                                    Text(it.Number,modifier = Modifier.weight(1f))
                                                }
                                            }
                                        }
                                    }
                                }
                              },
                              loading = {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                    )
                              },
                              modifier = Modifier
                                  .weight(1f)
                                  .fillMaxWidth()
                          )
                      }
                      if (selectDateExpand.value){
                          DatePickerDialog(
                              onDismissRequest = {
                                  selectDateExpand.value = false
                              },
                              confirmButton = {
                                  TextButton(
                                      onClick = {
                                          selectDateExpand.value = false
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
                              val buildForSend = selectBuild.value
                              buildForSend?:run {
                                  return@FloatingActionButton
                              }
                              val dateForSend = selectDate.value
                              dateForSend?:run {
                                  return@FloatingActionButton
                              }
                              emptyHouseVoyagerViewModel.getAvailableEmptyRoomData(
                                  campus = selectCampus.value,
                                  date = dateForSend,
                                  roomType = "普通教室",
                                  start = startClass.value.toString(),
                                  end = endClass.value.toString(),
                                  build = buildForSend
                              )
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
                      AnimatedVisibility(
                          visible = refresh.value,
                          exit = slideOutVertically {
                              return@slideOutVertically it
                          },
                          enter = slideInVertically {
                              return@slideInVertically it
                          },
                          modifier = Modifier
                              .matchParentSize(),
                      ){
                          Surface (
                              modifier = Modifier
                                  .matchParentSize()
                          ){
                              val data = remember {
                                  mutableStateOf<NetworkResult<ByteArray>>(NetworkResult.UnSend())
                              }
                              val cookie = remember {
                                  mutableStateOf<Cookie?>(null)
                              }
                              val action = remember {
                                  suspend {
                                      try {
                                          data.value = NetworkResult.LoadingWithAction()
                                          val client = HttpClient(){
                                              install(HttpCookies){
                                                  storage = CustomCookiesStorage(cookie)
                                              }
                                              configureForPlatform()
                                          }
                                          val image = client.get("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp").readBytes()
                                          data.value = NetworkResult.Success(image)
                                      }catch (e:Exception){
                                          data.value = NetworkResult.Error(e,Throwable("获取失败"))
                                          println(e.message)
                                      }
                                  }
                              }
                              LaunchedEffect(Unit){
                                  action.invoke()
                              }
                              val captcha = remember {
                                  mutableStateOf("")
                              }
                              Column (
                                  modifier = Modifier
                                      .fillMaxSize(),
                                  verticalArrangement = Arrangement.Center,
                                  horizontalAlignment = Alignment.CenterHorizontally
                              ){
                                  Row{
                                      Spacer(modifier = Modifier.weight(1f))
                                      Icon(
                                          Icons.Filled.Close,
                                          null,
                                          modifier = Modifier
                                              .size(50.dp)
                                              .wrapContentSize(Alignment.Center)
                                              .fillMaxSize(0.7f)
                                              .clickable {
                                                  refresh.value = false
                                              }
                                      )
                                  }
                                  data.CollectWithContentInBox(
                                      success = {
                                          Column (
                                              verticalArrangement = Arrangement.spacedBy(20.dp),
                                              horizontalAlignment = Alignment.CenterHorizontally
                                          ){
                                              Image(
                                                  bitmap = it.asImageBitmap(),
                                                  contentDescription = null,
                                                  modifier = Modifier
                                                      .clickable {
                                                          scope.launch {
                                                              action.invoke()
                                                          }
                                                      }
                                                      .fillMaxWidth(0.6f)
                                                      .aspectRatio(2f)
                                              )
                                              TextField(
                                                  captcha.value,
                                                  onValueChange = {
                                                      captcha.value = it
                                                  }
                                              )
                                              Button(
                                                  onClick = {
                                                      val verify = captcha.value
                                                      if (verify.isEmpty()){
                                                          toastState.addWarnToast("验证码不得为空")
                                                          return@Button
                                                      }
                                                      val cookieForSend = cookie.value
                                                      if (cookieForSend == null){
                                                          toastState.addWarnToast("请从新加载验证码")
                                                          return@Button
                                                      }
                                                      val buildForSend = selectBuild.value
                                                      buildForSend?:run {
                                                          return@Button
                                                      }
                                                      val dateForSend = selectDate.value
                                                      dateForSend?:run {
                                                          return@Button
                                                      }
                                                      refresh.value = false
                                                      emptyHouseVoyagerViewModel.refreshEmptyClassRoom(
                                                          verify = captcha.value,
                                                          code = cookieForSend.value,
                                                          campus = selectCampus.value,
                                                          date = dateForSend,
                                                          roomType = "普通教室",
                                                          start = startClass.value.toString(),
                                                          end = endClass.value.toString(),
                                                          build = buildForSend,
                                                          key = cookieForSend.name,
                                                      )
                                                  }
                                              ){
                                                  Text("获取")
                                              }
                                          }
                                      },
                                      error = {
                                          Text("获取失败", modifier = Modifier.clickable {
                                              scope.launch {
                                                  action.invoke()
                                              }
                                          })
                                      },
                                      loading = {
                                          CircularProgressIndicator()
                                      },
                                      modifier = Modifier
                                          .padding(20.dp)
                                          .fillMaxWidth()
                                          .weight(1f)
                                  )
                              }
                          }
                      }
                      EasyToast(toastState)
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





var campusList = mapOf(
    "旗山校区" to listOf(
        "全部",
        "第1学科群",
        "第2学科群",
        "第3学科群",
        "第4学科群",
        "第5学科群",
        "公共教学楼东1",
        "公共教学楼东2",
        "公共教学楼东3",
        "公共教学楼文科楼",
        "公共教学楼西1",
        "公共教学楼西2",
        "公共教学楼西3",
        "公共教学楼中楼",
        "国家科技园",
        "晋江楼",
        "素拓中心",
        "田径场",
        "图书馆",
        "音乐实训基地",
        "紫金教学楼",
    ),
    "晋江校区" to listOf(
        "全部",
        "A区",
        "B区中庭",
        "田径场",
    ),
    "铜盘校区" to listOf(
        "全部",
        "A楼",
        "B楼",
        "铜盘田径场",
    ),
    "怡山校区" to listOf(
        "全部",
        "北楼",
        "地矿楼",
        "电教",
        "电气楼",
        "东教",
        "管理学院",
        "机械楼",
        "计算机楼",
        "轻工楼",
        "田径场",
        "图书馆",
        "土建楼",
        "土乙楼",
        "文科楼",
        "物理楼",
        "西教",
        "至诚学院",
        "子兴楼",
    ),
    "泉港校区" to listOf(
        "全部",
        "泉港教学楼",
        "泉港实验一号楼",
    ),
    "厦门工艺美院" to listOf(
        "全部",
        "鼓浪屿校区",
        "集美校区",
    )
)