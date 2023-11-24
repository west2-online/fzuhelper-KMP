package ui.compose.Feedback

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FeedbackPost(
    modifier: Modifier = Modifier,
    submit: () -> Unit
){
    Column {
        val content = remember {
            mutableStateOf("")
        }
        val checkedState = remember { mutableStateOf(FeedbackType.Bug) }
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
                            checked = checkedState.value == feedback,
                            onCheckedChange = { checkedState.value = feedback }
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
                submit.invoke()
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

enum class FeedbackType(val describe:String){
    Bug("Bug 反馈"),
    Suggest("软件建议")
}
