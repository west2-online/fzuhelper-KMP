package ui.compose.Post

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.bumble.appyx.components.backstack.BackStack
import com.bumble.appyx.components.backstack.BackStackModel
import com.bumble.appyx.components.backstack.operation.push
import com.bumble.appyx.components.backstack.ui.slider.BackStackSlider
import com.bumble.appyx.navigation.composable.AppyxComponent
import com.bumble.appyx.navigation.modality.BuildContext
import com.bumble.appyx.navigation.node.Node
import com.bumble.appyx.navigation.node.ParentNode
import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import org.koin.compose.koinInject
import ui.compose.Report.ReportType
import ui.util.compose.EasyToast
import ui.util.compose.rememberToastState

@Composable
fun PostScreen(
    modifier: Modifier = Modifier,
    viewModel:PostListModel = koinInject(),
    state: LazyListState = rememberLazyListState()
){
    val toastState = rememberToastState()
    Crossfade(
        viewModel.currentItem.value
    ){
        when(it){
            is PostItem.PostList ->{
                PostList(
                    modifier = Modifier
                        .fillMaxSize(),
                    navigateToNewsDetail = {
                        viewModel.currentItem.value = PostItem.PostDetail(it)
                    },
                    navigateToRelease = {
                        viewModel.navigateToRelease()
                    },
                    state = state,
                    postListFlow = viewModel.postListFlow.collectAsLazyPagingItems(),
                    navigateToReport = {
                        viewModel.navigateToReport(ReportType.PostReportType(id = it.Id.toString(),data = it))
                    }
                )
            }
            is PostItem.PostDetail ->{
                Box(modifier = Modifier.fillMaxSize()){
                    LaunchedEffect(Unit){
                        viewModel.initPostCommentPreview(it.id)
                    }
                    viewModel.postCommentPreviewFlow.collectAsState().value?.let { page ->
                        PostDetail(
                            id = it.id,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
//                            back = {
//                                viewModel.currentItem.value = PostItem.PostList()
//                            },
                            postState = viewModel.currentPostDetail.collectAsState(),
                            getPostById = {
                                viewModel.getPostById(it)
                            },
                            postCommentPreview = page.flow,
                            postCommentTree = viewModel.postCommentTreeFlow,
                            getPostCommentTree = { treeStart ->
                                viewModel.getPostCommentTree(treeStart, postId = it.id)
                            },
                            submitComment = { parentId,postId,tree,content,image->
                              viewModel.submitComment(parentId,it.id.toInt(),tree,content,image)
                            },
                            commentSubmitState = viewModel.commentSubmitState.collectAsState(),
                            toastState = toastState,
                            commentReport = {
                                viewModel.navigateToReport(it)
                            }
                        )
                    }
                }
            }
        }
    }
    EasyToast(toastState)
}

sealed class PostNav:Parcelable{
    @Parcelize
    data object PostListNav : PostNav()

    @Parcelize
    class PostDetailNav(
        var postId:String
    ):PostNav()
}

class PostListNode(
    buildContext: BuildContext,
    val navigateToNewsDetail: (String) -> Unit,
):Node(
    buildContext = buildContext
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = koinInject<PostListModel>()
        PostList(
            modifier = Modifier
                .fillMaxSize(),
            navigateToNewsDetail = navigateToNewsDetail,
            navigateToRelease = {
                viewModel.navigateToRelease()
            },
            state = rememberLazyListState(),
            postListFlow = viewModel.postListFlow.collectAsLazyPagingItems(),
            navigateToReport = {
                viewModel.navigateToReport(ReportType.PostReportType(id = it.Id.toString(),data = it))
            }
        )
    }
}


class PostDetailNode(
    buildContext: BuildContext,
    private val postId: String
):Node(
    buildContext = buildContext,
){
    @Composable
    override fun View(modifier: Modifier) {
        val viewModel = koinInject<PostListModel>()
        val toastState = rememberToastState()
        Box(modifier = Modifier.fillMaxSize()){
            LaunchedEffect(Unit){
                viewModel.initPostCommentPreview(postId)
            }
            viewModel.postCommentPreviewFlow.collectAsState().value?.let { commentPreviewList ->
                PostDetail(
                    id = postId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    postState = viewModel.currentPostDetail.collectAsState(),
                    getPostById = {
                        viewModel.getPostById(it)
                    },
                    postCommentPreview = commentPreviewList.flow,
                    postCommentTree = viewModel.postCommentTreeFlow,
                    getPostCommentTree = { treeStart ->
                        viewModel.getPostCommentTree(treeStart, postId = postId)
                    },
                    submitComment = { parentId,postId,tree,content,image->
                        viewModel.submitComment(parentId,postId,tree,content,image)
                    },
                    commentSubmitState = viewModel.commentSubmitState.collectAsState(),
                    toastState = toastState,
                    commentReport = {
                        viewModel.navigateToReport(it)
                    },
                    refreshCommentPReview = {
                        viewModel.initPostCommentPreview(postId)
                    }
                )
            }
            EasyToast(toastState)
        }
    }
}

var postIdForSave : String? = null

class PostRouteTarget(
    buildContext: BuildContext,
    private val backStack: BackStack<PostNav> = BackStack<PostNav>(
        model = BackStackModel(
            initialTarget = PostNav.PostListNav ,
            savedStateMap = buildContext.savedStateMap,
        ),
        visualisation = { BackStackSlider(it) }
    ).apply {

        postIdForSave?.let {
            this.push(PostNav.PostDetailNav(it))
        }
    }
): ParentNode<PostNav>(
    appyxComponent = backStack,
    buildContext = buildContext,
) {
    override fun resolve(interactionTarget: PostNav, buildContext: BuildContext): Node  =
        when(interactionTarget){
            is PostNav.PostDetailNav -> PostDetailNode(
                buildContext,
                interactionTarget.postId
            )
            is PostNav.PostListNav -> PostListNode(
                    buildContext,
                    navigateToNewsDetail = {
                        backStack.push(PostNav.PostDetailNav(it))
                        postIdForSave = it
                        println(postIdForSave)
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


interface PostItem{
    class PostList():PostItem
    class PostDetail(var id:String):PostItem
}