package ui.compose.Action

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import config.BaseUrlConfig
import dev.icerock.moko.resources.ImageResource
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.example.library.MR
import org.koin.compose.koinInject
import ui.compose.Exam.ExamVoyagerScreen
import ui.compose.Test.TestVoyagerScreen
import ui.compose.Webview.GeneralWebViewVoyagerScreen
import ui.compose.emptyRoom.EmptyRoomVoyagerScreen
import ui.root.RootAction
import ui.root.tokenJump
import util.compose.ParentPaddingControl
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.math.takeover
import util.network.CollectWithContent
import kotlin.jvm.Transient

/**
 * 功能页的界面
 *
 * @param modifier Modifier
 */
@Composable
fun Action(modifier: Modifier) {
  Column(modifier = modifier) {
    val rootAction = koinInject<RootAction>()
    Carousel(modifier = Modifier.fillMaxWidth().aspectRatio(2f).clip(RoundedCornerShape(10.dp)))
    LazyVerticalGrid(
      modifier =
        Modifier.padding(top = 10.dp).weight(1f).fillMaxWidth().clip(RoundedCornerShape(10.dp)),
      columns = GridCells.Fixed(5),
    ) {
      items(Functions.entries.size) {
        Column(
          modifier =
          Modifier.fillMaxWidth()
            .aspectRatio(0.7f)
            .clip(RoundedCornerShape(10.dp))
            .clickable { Functions.entries[it].navigator.invoke(rootAction) }
            .padding(10.dp),
        ) {
          Image(
            painter = painterResource(Functions.entries[it].painter),
            null,
            modifier =
              Modifier.fillMaxWidth()
                .aspectRatio(1f)
                .wrapContentSize(Alignment.Center)
                .fillMaxSize(0.7f)
                .clip(RoundedCornerShape(10)),
            contentScale = ContentScale.FillBounds,
          )
          Text(
            Functions.entries[it].functionName,
            modifier = Modifier.fillMaxWidth().weight(1f),
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            fontSize = 10.sp,
          )
        }
      }
    }
  }
}

/**
 * 轮播图界面
 *
 * @param modifier Modifier
 * @param refreshCarousel Function0<Unit> 刷新轮播图的逻辑
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Carousel(modifier: Modifier = Modifier, refreshCarousel: () -> Unit = {}) {
  val viewModel = koinInject<ActionViewModel>()
  LaunchedEffect(Unit) { viewModel.initRibbonList() }
  Box(modifier = modifier) {
    viewModel.ribbonList
      .collectAsState()
      .CollectWithContent(
        loading = {
          Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
          }
        },
        success = { ribbonDataList ->
          val pageState =
            rememberPagerState(initialPage = 0) {
              if (ribbonDataList != null) {
                return@rememberPagerState ribbonDataList.size
              }
              0
            }
          val coroutineScope = rememberCoroutineScope()

          LaunchedEffect(pageState.currentPage) {
            while (true) {
              delay(4000)
              coroutineScope.launch {
                if (ribbonDataList != null) {
                  pageState.animateScrollToPage(
                    (pageState.currentPage + 1).takeover(ribbonDataList.size) ?: 0
                  )
                }
              }
            }
          }
          HorizontalPager(state = pageState) { index ->
            val scope = rememberCoroutineScope()
            val rootAction = koinInject<RootAction>()
            ribbonDataList?.let {
              KamelImage(
                resource = asyncPainterResource("${BaseUrlConfig.RibbonImage}/${it[index].Image}"),
                null,
                modifier =
                  Modifier.fillMaxSize().clickable {
                    tokenJump(
                      tokenForParse = ribbonDataList[index].Action,
                      scope = scope,
                      fail = {},
                      rootAction = rootAction,
                    )
                  },
                contentScale = ContentScale.FillBounds,
              )
            }
          }
        },
        error = {
          Row(
            modifier = Modifier.fillMaxSize().clickable { viewModel.getRibbonList() },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
          ) {
            Icon(
              painter = painterResource(MR.images.close),
              contentDescription = null,
              tint = Color.Red,
              modifier = Modifier.size(50.dp),
            )
            Text("加载失败", modifier = Modifier.padding(start = 10.dp))
          }
        },
        unSend = { Box(modifier = Modifier.fillMaxSize()) },
      )
  }
}

/**
 * 可用功能的枚举类
 *
 * @property functionName String 功能名
 * @property painter ImageResource 功能的图标
 * @property navigator Function1<RootAction, Unit> 点击功能的逻辑
 * @constructor
 */
enum class Functions(
  val functionName: String,
  val painter: ImageResource,
  val navigator: (RootAction) -> Unit,
) {
  //  QRCODE(
//    functionName = "二维码生成",
//    painter = MR.images.qrcode,
//    { rootAction -> rootAction.navigateFromActionToQRCodeScreen() },
//  ),
  //    WebView( functionName = "新生宝典", painter = MR.images.login, { rootAction -> }),
//  Weather(
//    functionName = "天气",
//    painter = MR.images.cloud,
//    { rootAction -> rootAction.navigateFromAnywhereToWeather() },
//  ),
  //    Map(  functionName = "地图", painter = MR.images.close, { rootAction -> }),
  Test(
    functionName = "测试",
    painter = MR.images.close,
    { rootAction -> rootAction.navigateToScreen(TestVoyagerScreen()) },
  ),

  //  AboutUs(
//    functionName = "关于我们",
//    painter = MR.images.FuTalk,
//    { rootAction -> rootAction.navigateFromActionToAboutUs() },
//  ),
//  Manage(
//    functionName = "管理",
//    painter = MR.images.not_solved,
//    { rootAction -> rootAction.navigateFromAnywhereToManage() },
//  ),
  Exam(
    functionName = "考场查询",
    painter = MR.images.exam,
    { rootAction -> rootAction.navigateToScreen(ExamVoyagerScreen()) },
  ),
  Feedback(
    functionName = "反馈",
    painter = MR.images.feedback2,
    { rootAction -> rootAction.navigateFromActionToFeedback() },
  ),
  Setting(
    functionName = "设置",
    painter = MR.images.setting,
    { rootAction -> rootAction.navigateFormAnywhereToSetting() },
  ),
  Log(
    functionName = "日志",
    painter = MR.images.log,
    { rootAction -> rootAction.navigateFormAnywhereToLog() },
  ),
  EmptyHouse(
    functionName = "空教室",
    painter = MR.images.ic_empty_room,
    navigator = { rootAction -> rootAction.navigateToScreen(EmptyRoomVoyagerScreen()) },
  ),
  ChangeMajors(
    functionName = "飞跃手册",
    painter = MR.images.ic_run,
    navigator = { rootAction ->
      rootAction.navigateToScreen(GeneralWebViewVoyagerScreen("https://run.west2.online/"))
    },
  ),
  //    OfficialWebsite(functionName = "转专业", painter =MR.images.school, navigator = { rootAction ->
  // rootAction.navigateToScreen(WebViewVoyagerScreen("https://futalker.github.io/")) })
}

/**
 * 功能页的一级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @property options TabOptions
 * @constructor
 */
class ActionVoyagerScreen(
  @Transient private val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Tab {
  override val options: TabOptions
    @Composable
    get() {
      return TabOptions(index = 0u, title = "")
    }

  @Composable
  override fun Content() {
    Action(
      modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl).padding(10.dp)
    )
  }
}
