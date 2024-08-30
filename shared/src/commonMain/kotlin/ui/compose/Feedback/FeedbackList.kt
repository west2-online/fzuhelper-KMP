package ui.compose.Feedback

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import data.feedback.github.githubIssueListByPage.GithubIssueByPageItem
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlin.jvm.Transient
import kotlinx.coroutines.flow.filter
import org.example.library.MR
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.Label
import util.compose.ParentPaddingControl
import util.compose.ThemeCard
import util.compose.Toast
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState

/**
 * 反馈列表显示
 *
 * @param modifier Modifier
 * @param navigateToDetail Function1<[@kotlin.ParameterName] Int, Unit>
 * @param navigateToPost Function0<Unit>
 * @param feedbackListFlow LazyPagingItems<Data>
 * @param toastState Toast
 */
@Composable
fun FeedbackList(
  modifier: Modifier,
  navigateToDetail: (id: Int) -> Unit,
  navigateToPost: () -> Unit,
  feedbackListFlow: LazyPagingItems<GithubIssueByPageItem>,
  toastState: Toast,
) {
  val isRefresh = remember { mutableStateOf(false) }
  val state = rememberLazyListState()
  LaunchedEffect(state) {
    snapshotFlow { state.isScrollInProgress && !state.canScrollBackward }
      .filter { it }
      .collect {
        isRefresh.value = true
        feedbackListFlow.refresh()
        isRefresh.value = false
      }
  }

  Box(modifier = modifier) {
    LazyColumn(modifier = Modifier.fillMaxSize(), state = state) {
      items(feedbackListFlow.itemCount) {
        feedbackListFlow[it]?.let {
          FeedbackListItem(navigateToDetail = navigateToDetail, feedback = it)
        }
      }
      feedbackListFlow.loadState.apply {
        when {
          refresh is LoadState.Loading || append is LoadState.Loading -> {
            item {
              Box(modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator(
                  modifier = Modifier.padding(5.dp).align(Alignment.Center).size(30.dp)
                )
              }
            }
          }
          refresh is LoadState.Error || append is LoadState.Error -> {
            item {
              Box(
                modifier =
                  Modifier.fillMaxWidth()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Red)
                    .padding(10.dp)
              ) {
                Text(
                  modifier = Modifier.padding(vertical = 3.dp).fillMaxWidth(),
                  text = "加载失败",
                  textAlign = TextAlign.Center,
                )
              }
            }
          }
          append is LoadState.NotLoading -> {
            item {
              Box(
                modifier =
                  Modifier.fillMaxWidth()
                    .padding(4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.Gray)
                    .padding(10.dp)
              ) {
                Text(
                  modifier = Modifier.padding(vertical = 3.dp).fillMaxWidth(),
                  text = "已经到底了",
                  textAlign = TextAlign.Center,
                )
              }
            }
          }
        }
      }
    }
    FloatingActionButton(
      onClick = { navigateToPost.invoke() },
      modifier =
        Modifier.offset(x = (-15).dp, y = ((-15).dp)).size(50.dp).align(Alignment.BottomEnd),
      shape = CircleShape,
    ) {
      Icon(
        modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.Center).fillMaxSize(0.5f),
        imageVector = Icons.Filled.Add,
        contentDescription = null,
      )
    }
    AnimatedVisibility(
      isRefresh.value,
      modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
      exit = slideOutVertically(),
      enter = slideInVertically(),
    ) {
      Box(modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter)) {
        CircularProgressIndicator(
          modifier = Modifier.padding(5.dp).align(Alignment.Center).size(30.dp)
        )
      }
    }
  }
}

/**
 * 反馈列表中的item
 *
 * @param navigateToDetail Function1<[@kotlin.ParameterName] Int, Unit>
 * @param feedback Data
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FeedbackListItem(navigateToDetail: (id: Int) -> Unit, feedback: GithubIssueByPageItem) {
  ThemeCard(
    cardModifier =
      Modifier.fillMaxWidth()
        .wrapContentHeight()
        .clickable { navigateToDetail(feedback.number) }
        .padding(10.dp),
    columnModifier = Modifier.wrapContentHeight().fillMaxWidth().padding(10.dp),
    shape = RoundedCornerShape(10.dp),
  ) {
    DiscussInList(feedback = feedback)
    Box(modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
      FlowRow {
        feedback.labels.forEach { label ->
          Label(label.name ?: "label", color = (label.color ?: "#808080").hexToRgb())
        }
      }
    }
  }
}

fun String.hexToRgb(): Color {
  // 确保输入的十六进制字符串是6个字符长
  require(this.length == 6) { "Invalid hex color string length. Must be 6 characters long." }

  // 分解成 RGB 分量
  val red = this.substring(0, 2).toInt(16)
  val green = this.substring(2, 4).toInt(16)
  val blue = this.substring(4, 6).toInt(16)

  return Color(red, green, blue)
}

@Composable
fun DiscussInList(feedback: GithubIssueByPageItem) {
  Column {
    Row(Modifier.fillMaxWidth().wrapContentHeight().padding(top = 10.dp)) {
      KamelImage(
        resource = asyncPainterResource(feedback.toAvatar()),
        null,
        modifier = Modifier.padding(end = 10.dp).width(50.dp).aspectRatio(1f).clip(CircleShape),
        contentScale = ContentScale.FillBounds,
      )
      Column(modifier = Modifier.weight(1f).wrapContentHeight()) {
        Text(feedback.created_at, fontSize = 10.sp, modifier = Modifier.padding(bottom = 3.dp))
        Text(feedback.title, modifier = Modifier)
      }
    }
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(vertical = 10.dp),
    ) {
      Icon(
        painter =
          painterResource(
            if (feedback.state == "open") MR.images.activeStatus else MR.images.not_solved
          ),
        "",
        modifier =
          Modifier.size(50.dp)
            .wrapContentSize(Alignment.Center)
            .fillMaxSize(0.7f)
            .clip(CircleShape)
            .background(if (feedback.state == "open") Color.Green else Color.Red)
            .wrapContentSize(Alignment.Center)
            .fillMaxSize(0.7f),
      )
      Text(if (feedback.state == "open") "活跃中" else "已关闭")
    }
  }
}

/**
 * 反馈列表 二级 屏幕
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class FeedbackListVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val viewModel: FeedBackViewModel = koinInject()
    val toastState = rememberToastState()
    FeedbackList(
      modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl),
      navigateToDetail = { feedbackId -> navigator.push(FeedbackDetailVoyagerScreen(feedbackId)) },
      navigateToPost = { navigator.push(FeedbackPostVoyagerScreen()) },
      toastState = toastState,
      feedbackListFlow = viewModel.feedbackListFlow.collectAsLazyPagingItems(),
    )
    EasyToast(toastState)
  }
}
