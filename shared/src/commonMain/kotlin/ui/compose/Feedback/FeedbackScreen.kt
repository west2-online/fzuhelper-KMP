package ui.compose.Feedback

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.cash.paging.compose.collectAsLazyPagingItems
import org.koin.compose.koinInject
import ui.util.compose.EasyToast
import ui.util.compose.rememberToastState

@Composable
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    viewModel: FeedBackViewModel = koinInject()
){

    val currentItem = remember {
        mutableStateOf<FeedbackItem>(FeedbackItem.Feedback())
    }
    val toastState = rememberToastState()
    Surface {
        Box{
            Crossfade(
                currentItem.value
            ) { it ->
                when (it) {
                    is FeedbackItem.Feedback -> {
                        FeedbackList(
                            modifier = Modifier
                                .fillMaxSize(),
                            navigateToDetail = { id ->
                                currentItem.value = FeedbackItem.FeedbackDetail(id)
                            },
                            navigateToPost = {
                                currentItem.value = FeedbackItem.FeedbackPost()
                            },
                            toastState = toastState,
                            feedbackListFlow = viewModel.postListFlow.collectAsLazyPagingItems()
                        )
                    }

                    is FeedbackItem.FeedbackDetail -> {
                        FeedbackDetail(
                            modifier = Modifier
                                .fillMaxSize(),
                            back = {
                                currentItem.value = FeedbackItem.Feedback()
                            },
                            getDetailData = {
                                viewModel.getFeedbackDetail(it.id)
                            },
                            toastState = toastState,
                            postNewComment = { content ->
                                  viewModel.postFeedbackDetailComment(content = content, id = it.id)
                            },
                            commentState = viewModel.commentResult.collectAsState()
                            ,
                            detailState = viewModel.detailResult.collectAsState(),
                        )
                    }

                    is FeedbackItem.FeedbackPost -> {
                        FeedbackPost(
                            modifier = Modifier
                                .fillMaxSize(),
                            submit = { content, type ->
                                viewModel.submitNewFeedback(content, type)
                            },
                            submitResult = viewModel.submitResult.collectAsState(),
                            toastState = toastState,
                            back = {
                                currentItem.value = FeedbackItem.Feedback()
                            }
                        )
                    }
                }
            }
            EasyToast(toast = toastState)
        }
    }
}


interface FeedbackItem{
    class Feedback():FeedbackItem
    class FeedbackDetail(var id:Int):FeedbackItem
    class FeedbackPost():FeedbackItem
}