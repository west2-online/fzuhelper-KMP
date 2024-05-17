package ui.compose.Feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
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
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.ParentPaddingControl
import util.compose.Toast
import util.compose.defaultSelfPaddingControl
import util.compose.parentSystemControl
import util.compose.rememberToastState
import util.network.NetworkResult
import util.network.toast
import kotlin.jvm.Transient

@Composable
fun FeedbackPost(
    modifier: Modifier = Modifier,
    submit: (content: String, type: FeedbackType) -> Unit,
    toastState: Toast,
    submitResult: State<NetworkResult<String>>
){
    LaunchedEffect(submitResult.value.key.value){
        submitResult.value.toast(
            success = {
                toastState.addToast("发布成功")
            },
            error = {
                toastState.addWarnToast("发布失败")
            }
        )
    }

    Column (
        modifier = modifier
    ){
        val content = remember {
            mutableStateOf("")
        }
        val feedbackType = remember { mutableStateOf(FeedbackType.Bug) }
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            item{
                TextField(
                    value = content.value,
                    onValueChange = {
                        content.value = it
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                )
            }
            item {
                FeedbackType.values().forEach { feedback->
                    Row (
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Checkbox(
                            checked = feedbackType.value == feedback,
                            onCheckedChange = { feedbackType.value = feedback }
                        )
                        Text(
                            feedback.describe,
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentHeight()
                        )
                    }
                }

            }
        }
        Button(
            onClick = {
                submit.invoke(content.value,feedbackType.value)
            },
            modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .wrapContentHeight()
        ){
            Text("提交")
        }
    }
}

enum class FeedbackType(val describe:String,val code:Int){
    Bug("Bug 反馈", code = 0),
    Suggest("软件建议", code = 1)
}

class FeedbackPostVoyagerScreen(
    @Transient
    val parentPaddingControl: ParentPaddingControl = defaultSelfPaddingControl()
):Screen{
    @Composable
    override fun Content() {
        val viewModel: FeedBackViewModel = koinInject()
        val toastState = rememberToastState()
        FeedbackPost(
            modifier = Modifier
                .fillMaxSize()
                .parentSystemControl(parentPaddingControl),
            submit = { content, type ->
                viewModel.submitNewFeedback(content, type)
            },
            submitResult = viewModel.submitResult.collectAsState(),
            toastState = toastState,
        )
        EasyToast(toastState)
    }
}