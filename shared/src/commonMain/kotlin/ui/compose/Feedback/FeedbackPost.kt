package ui.compose.Feedback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import kotlin.jvm.Transient
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.Toast
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.network.CollectWithContent
import util.network.NetworkResult
import util.network.toast

/**
 * 发布新的反馈
 *
 * @param modifier Modifier
 * @param submit Function2<[@kotlin.ParameterName] String, [@kotlin.ParameterName] FeedbackType,
 *   Unit> 发布逻辑
 * @param toastState Toast
 * @param submitResult State<NetworkResult<String>> 发布结果
 */
@Composable
fun FeedbackPost(
  modifier: Modifier = Modifier,
  submit: (content: String, title: String, label: List<String>) -> Unit,
  toastState: Toast,
  submitResult: State<NetworkResult<String>>,
) {
  val content = remember { mutableStateOf("") }
  val title = remember { mutableStateOf("") }
  LaunchedEffect(submitResult.value.key.value) {
    submitResult.value.toast(
      success = {
        content.value = ""
        title.value = ""
        toastState.addToast("发布成功")
      },
      error = { toastState.addWarnToast(it.message.toString()) },
    )
  }
  Column(modifier = modifier) {
    val feedbackType = remember { mutableStateOf(FeedbackType.ANDROID) }
    LazyColumn(
      modifier = Modifier.weight(1f).fillMaxWidth().padding(10.dp),
      verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
      item {
        TextField(
          value = title.value,
          onValueChange = { title.value = it },
          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          placeholder = { Text("标题") },
        )
      }
      item {
        TextField(
          value = content.value,
          onValueChange = { content.value = it },
          modifier = Modifier.fillMaxWidth().wrapContentHeight(),
          placeholder = { Text("正文") },
        )
      }
      item {
        FeedbackType.entries.forEach { feedback ->
          Row(
            modifier = Modifier.wrapContentHeight().fillMaxWidth().padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
          ) {
            Checkbox(
              checked = feedbackType.value == feedback,
              onCheckedChange = { feedbackType.value = feedback },
            )
            Text(feedback.describe, modifier = Modifier.weight(1f).wrapContentHeight())
          }
        }
      }
    }
    Button(
      onClick = { submit.invoke(content.value, title.value, listOf(feedbackType.value.label)) },
      modifier = Modifier.padding(10.dp).fillMaxWidth().wrapContentHeight(),
      contentPadding = PaddingValues(10.dp),
    ) {
      submitResult.CollectWithContent(
        loading = { CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.Red) },
        modifier = Modifier.wrapContentSize().padding(5.dp),
      )
      Text("提交")
    }
  }
}

/**
 * 可选的反馈类型
 *
 * @property describe String
 * @property code Int 用于发送到后端的值
 * @constructor
 */
enum class FeedbackType(val describe: String, val code: Int, val label: String) {
  IOS("IOS(苹果)", code = 0, label = "IOS"),
  ANDROID("ANDROID(安卓)", code = 1, label = "Android"),
}

/**
 * 发布反馈界面 二级界面
 *
 * @property parentPaddingControl ParentPaddingControl
 * @constructor
 */
class FeedbackPostVoyagerScreen(
  @Transient val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
) : Screen {
  @Composable
  override fun Content() {
    val viewModel: FeedBackViewModel = koinInject()
    val toastState = rememberToastState()
    FeedbackPost(
      modifier = Modifier.fillMaxSize().parentSystemControl(parentPaddingControl),
      submit = { content, title, labelList ->
        viewModel.submitNewFeedback(content, title, labelList)
      },
      submitResult = viewModel.submitResult.collectAsState(),
      toastState = toastState,
    )
    EasyToast(toastState)
  }
}
