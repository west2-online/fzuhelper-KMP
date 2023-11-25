package ui.compose.Feedback

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.compose.koinInject

@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedBackViewModel = koinInject()
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
                    },
                    feedbackListFlow = viewModel.postListFlow.collectAsLazyPagingItems()
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
                    submit = { content,type ->
                        viewModel.submitNewFeedback(content,type)
                    },
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
    class FeedbackPost():FeedbackItem
}