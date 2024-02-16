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
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.pop
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.fader.BackStackFader
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import org.koin.compose.koinInject
import util.compose.EasyToast
import util.compose.rememberToastState

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

sealed class FeedbackTarget:Parcelable{
    @Parcelize
    data object FeedbackList:FeedbackTarget()

    @Parcelize
    class FeedbackDetail(
        val feedbackId :Int
    ):FeedbackTarget()

    @Parcelize
    data object FeedbackPost:FeedbackTarget()
}

class FeedbackAssemblyNode(
    val buildContext: BuildContext,
    private val backStack: BackStack<FeedbackTarget> = BackStack(
        model = BackStackModel(
            initialTarget = FeedbackTarget.FeedbackList,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackFader(it) }
    )
):ParentNode<FeedbackTarget>(
    buildContext = buildContext,
    appyxComponent = backStack
){
    override fun resolve(interactionTarget: FeedbackTarget, buildContext: BuildContext): Node =
        when(interactionTarget){
            is FeedbackTarget.FeedbackList -> FeedbackList(
                buildContext = buildContext,
                navigateToPost = {
                    backStack.push(FeedbackTarget.FeedbackPost)
                },
                navigateToDetail = {
                    backStack.push(FeedbackTarget.FeedbackDetail(it))
                }
            )
            is FeedbackTarget.FeedbackDetail -> FeedbackDetail(
                buildContext = buildContext,
                feedbackId = interactionTarget.feedbackId,
                back = {
                    backStack.pop()
                }
            )
            is FeedbackTarget.FeedbackPost -> FeedbackPost(
                buildContext,
                back = {
                    backStack.pop()
                }
            )
        }

    @Composable
    override fun View(modifier: Modifier) {
        AppyxComponent(
            appyxComponent = backStack,
            modifier = modifier
        )
    }
}

class FeedbackList(
    buildContext: BuildContext,
    val navigateToDetail: (id: Int) -> Unit,
    val navigateToPost: () -> Unit,
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: FeedBackViewModel = koinInject()
        val toastState = rememberToastState()
        FeedbackList(
            modifier = Modifier
                .fillMaxSize(),
            navigateToDetail = navigateToDetail,
            navigateToPost = navigateToPost,
            toastState = toastState,
            feedbackListFlow = viewModel.postListFlow.collectAsLazyPagingItems()
        )
        EasyToast(toastState)
    }
}

class FeedbackDetail(
    val buildContext: BuildContext,
    private val feedbackId:Int,
    private val back: (() -> Unit)?,
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: FeedBackViewModel = koinInject()
        val toastState = rememberToastState()
        FeedbackDetail(
            modifier = Modifier
                .fillMaxSize(),
            back = back,
            getDetailData = {
                viewModel.getFeedbackDetail(feedbackId)
            },
            toastState = toastState,
            postNewComment = { content ->
                viewModel.postFeedbackDetailComment(content = content, id = feedbackId)
            },
            commentState = viewModel.commentResult.collectAsState(),
            detailState = viewModel.detailResult.collectAsState(),
        )
        EasyToast(toastState)
    }
}

class FeedbackPost(
    buildContext: BuildContext,
    private val back:()->Unit
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel: FeedBackViewModel = koinInject()
        val toastState = rememberToastState()
        FeedbackPost(
            modifier = Modifier
                .fillMaxSize(),
            submit = { content, type ->
                viewModel.submitNewFeedback(content, type)
            },
            submitResult = viewModel.submitResult.collectAsState(),
            toastState = toastState,
            back = {
                back.invoke()
            }
        )
        EasyToast(toastState)
    }
}