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
                    }
                )
            }
            is FeedbackItem.FeedbackDetail ->{
                FeedbackDetail(
                    modifier = Modifier
                        .fillMaxSize(),
                    back = {
                        currentItem.value = FeedbackItem.Feedback()
                    }
                )
            }
        }
    }
}


interface FeedbackItem{
    class Feedback():FeedbackItem
    class FeedbackDetail(var id:String):FeedbackItem
}