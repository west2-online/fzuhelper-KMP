package ui.compose.Post

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import data.post.PostById.PostById
import data.post.PostCommentPreview.Data
import data.post.PostCommentPreview.PostCommentPreview
import data.post.PostCommentTree.PostCommentTree
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import doist.x.normalize.Form
import doist.x.normalize.normalize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import repository.PostRepository
import ui.compose.Report.ReportType
import ui.root.RootAction
import util.flow.actionWithLabel
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.logicIfUnSend
import util.network.networkError
import util.network.reset

class PostDetailViewModel(
    private val client: HttpClient,
    private val postRepository : PostRepository,
    private val rootAction: RootAction
):ViewModel() {

    private val _currentPostDetail = CMutableStateFlow(MutableStateFlow<NetworkResult<PostById>>(
        NetworkResult.UnSend()
    ))
    val currentPostDetail = _currentPostDetail.asStateFlow()

    private val _postCommentPreviewFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, Data>?>(null))
    var postCommentPreviewFlow = _postCommentPreviewFlow.asStateFlow()

    private val _postCommentTreeFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, data.post.PostCommentTree.Data>?>(null))
    var postCommentTreeFlow = _postCommentTreeFlow.asStateFlow()

    private val _commentSubmitState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val commentSubmitState = _commentSubmitState.asStateFlow()

    private val _postLikeSubmitState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val postLikeSubmitState = _postLikeSubmitState.asStateFlow()


    fun initPostCommentPreview(postId: String){
        viewModelScope.launchInDefault {
            _postCommentPreviewFlow.value = Pager(
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2
                ),
            ){
                EasyPageSourceForCommentPreview(
                    backend = LoadPageDataForCommentPreview {
                        val postCommentPreview = client.get("post/comment/page/${it}/${postId}").body<PostCommentPreview>()
                        return@LoadPageDataForCommentPreview postCommentPreview.data
                    }
                )
            }
        }
    }

    private fun getPostById(id: String){
        viewModelScope.launch (Dispatchers.IO){
            _currentPostDetail.logicIfNotLoading {
                postRepository.getPostById(id = id)
                    .actionWithLabel(
                        label = "getPostById",
                        catchAction = {
                            _currentPostDetail.reset(NetworkResult.Error(Throwable("帖子获取失败")))
                        },
                        collectAction = {
                            _currentPostDetail.reset(NetworkResult.Success(it))
                        }
                    )

            }
        }
    }

    fun initPostById(postId: String){
        _currentPostDetail.logicIfUnSend (
            block = {
                getPostById(postId)
            }
        )
    }

    fun refreshPostById(postId: String){
        getPostById(postId)
    }

    fun submitComment(parentId:Int,postId:Int,tree:String,content:String,image:ByteArray?){
        viewModelScope.launchInDefault {
            _commentSubmitState.logicIfNotLoading {
                postRepository.postNewComment(parentId,postId,tree,content.normalize(Form.NFC),image)
                    .catchWithMassage {
                        _commentSubmitState.reset(NetworkResult.Error(Throwable("评论失败，稍后再试")))
                    }.collectWithMassage{
                        _commentSubmitState.reset(it.toNetworkResult())
                    }
            }
        }
    }

    fun navigateToReport(type: ReportType){
        rootAction.navigateFormPostToReport(type)
    }

    fun getPostCommentTree(treeStart: String,postId:String){
        viewModelScope.launch {
            _postCommentTreeFlow.value = Pager(
                PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 2
                ),
            ){
                EasyPageSourceForCommentTree(
                    backend = LoadPageDataForCommentTree {
                        return@LoadPageDataForCommentTree client.get("post/commentList/page/${it}/${treeStart}/${postId}").body<PostCommentTree>().data
                    }
                )
            }
        }
    }

    fun postLikes(postId: Int){
        viewModelScope.launchInDefault {
            _postLikeSubmitState.logicIfNotLoading {
                postRepository.postLike(postId)
                    .catchWithMassage {
                        _postLikeSubmitState.reset(networkError("点赞失败"))
                    }
                    .collectWithMassage {
                        _postLikeSubmitState.reset(it.toNetworkResult())
                    }
            }
        }
    }
}