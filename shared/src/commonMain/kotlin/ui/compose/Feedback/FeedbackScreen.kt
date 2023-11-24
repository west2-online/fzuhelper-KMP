package ui.compose.Feedback

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier
){
    val currentItem = remember {
        mutableStateOf<FeedbackItem>(FeedbackItem.Feedback())
    }
    Crossfade(
        currentItem.value
    ){ it ->
        when(it){
            is FeedbackItem.Feedback ->{
                FeedbackList(
                    modifier = Modifier
                        .fillMaxSize(),
                    navigateToDetail = { id->
                        currentItem.value = FeedbackItem.FeedbackDetail(id)
                    },
                    navigateToPost = {
                        currentItem.value = FeedbackItem.FeedbackPost()
                    }
                )
            }
            is FeedbackItem.FeedbackDetail ->{
                FeedbackDetail(
                    modifier = Modifier
                        .fillMaxSize(),
                    back = {
                        currentItem.value = FeedbackItem.Feedback()
                    },
                )
            }
            is FeedbackItem.FeedbackPost ->{
                FeedbackPost(
                    modifier = Modifier
                        .fillMaxSize(),
                    submit = {

                    }
                )
            }
        }
    }
}


interface FeedbackItem{
    class Feedback():FeedbackItem
    class FeedbackDetail(var id:String):FeedbackItem
    class FeedbackPost():FeedbackItem
}