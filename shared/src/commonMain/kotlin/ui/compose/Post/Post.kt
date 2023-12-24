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
import org.koin.compose.koinInject
import ui.compose.Report.ReportType
import ui.util.compose.EasyToast
import ui.util.compose.rememberToastState

@Composable
fun NewScreen(
    modifier: Modifier = Modifier,
    viewModel:NewViewModel = koinInject(),
    state: LazyListState = rememberLazyListState()
){

//    LaunchedEffect(Unit){
//        viewModel.getPostByPage("1")
//    }
    val toastState = rememberToastState()

    Crossfade(
        viewModel.currentItem.value
    ){
        when(it){
            is NewItem.NewList ->{
                NewsList(
                    modifier = Modifier
                        .fillMaxSize(),
                    navigateToNewsDetail = {
                        viewModel.currentItem.value = NewItem.NewDetail(it)
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
            is NewItem.NewDetail ->{
                Box(modifier = Modifier.fillMaxSize()){
                    LaunchedEffect(Unit){
                        viewModel.getPostCommentPreview(it.id)
                    }
                    viewModel.postCommentPreviewFlow.collectAsState().value?.let { page ->
                        NewsDetail(
                            id = it.id,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            back = {
                                viewModel.currentItem.value = NewItem.NewList()
                            },
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


interface NewItem{
    class NewList():NewItem
    class NewDetail(var id:String):NewItem
}