package ui.compose.Post

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import data.post.PostById.PostData
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
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 帖子详情的逻辑
 *
 * @property client HttpClient
 * @property postRepository PostRepository
 * @property rootAction RootAction
 * @property _currentPostDetail CMutableStateFlow<NetworkResult<PostData>>
 * @property currentPostDetail StateFlow<NetworkResult<PostData>> 帖子详情的结果
 * @property _postCommentPreviewFlow CMutableStateFlow<Pager<Int, Data>?>
 * @property postCommentPreviewFlow StateFlow<Pager<Int, Data>?> 帖子评论预览的结果
 * @property _postCommentTreeFlow CMutableStateFlow<Pager<Int, Data>?>
 * @property postCommentTreeFlow StateFlow<Pager<Int, Data>?> 帖子评论树的结果
 * @property _commentSubmitState CMutableStateFlow<NetworkResult<String>>
 * @property commentSubmitState StateFlow<NetworkResult<String>> 帖子发布的结果
 * @property _postLikeSubmitState CMutableStateFlow<NetworkResult<String>>
 * @property postLikeSubmitState StateFlow<NetworkResult<String>> 点赞帖子的结果
 * @constructor
 */
class PostDetailViewModel(
  private val client: HttpClient,
  private val postRepository: PostRepository,
  private val rootAction: RootAction,
) : ViewModel() {

  private val _currentPostDetail =
    CMutableStateFlow(MutableStateFlow<NetworkResult<PostData>>(NetworkResult.UnSend()))
  val currentPostDetail = _currentPostDetail.asStateFlow()

  private val _postCommentPreviewFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, Data>?>(null))
  var postCommentPreviewFlow = _postCommentPreviewFlow.asStateFlow()

  private val _postCommentTreeFlow =
    CMutableStateFlow(MutableStateFlow<Pager<Int, data.post.PostCommentTree.Data>?>(null))
  var postCommentTreeFlow = _postCommentTreeFlow.asStateFlow()

  private val _commentSubmitState =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val commentSubmitState = _commentSubmitState.asStateFlow()

  private val _postLikeSubmitState =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val postLikeSubmitState = _postLikeSubmitState.asStateFlow()

  fun initPostCommentPreview(postId: String) {
    viewModelScope.launchInDefault {
      _postCommentPreviewFlow.value =
        Pager(PagingConfig(pageSize = 10, prefetchDistance = 2)) {
          EasyPageSourceForCommentPreview(
            backend =
              LoadPageDataForCommentPreview {
                val postCommentPreview =
                  client.get("post/comment/page/${it}/${postId}").body<PostCommentPreview>()
                return@LoadPageDataForCommentPreview postCommentPreview.data
              }
          )
        }
    }
  }

  /**
   * 根据id获取帖子详情
   *
   * @param id String
   */
  private fun getPostById(id: String) {
    viewModelScope.launch(Dispatchers.IO) {
      _currentPostDetail.logicIfNotLoading {
        postRepository
          .getPostById(id = id)
          .actionWithLabel(
            label = "getPostById",
            catchAction = { label, error ->
              _currentPostDetail.resetWithLog(label, networkErrorWithLog(error, "帖子获取失败"))
            },
            collectAction = { label, data ->
              _currentPostDetail.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }

  //    fun initPostById(postId: String){
  //        _currentPostDetail.logicIfUnSend (
  //            block = {
  //                getPostById(postId)
  //            }
  //        )
  //    }

  /**
   * 根据id 刷新帖子
   *
   * @param postId String
   */
  fun refreshPostById(postId: String) {
    getPostById(postId)
  }

  /**
   * 发布对帖子的评论
   *
   * @param parentId Int
   * @param postId Int
   * @param tree String
   * @param content String
   * @param image ByteArray?
   */
  fun submitComment(parentId: Int, postId: Int, tree: String, content: String, image: ByteArray?) {
    viewModelScope.launchInDefault {
      _commentSubmitState.logicIfNotLoading {
        postRepository
          .postNewComment(parentId, postId, tree, content.normalize(Form.NFC), image)
          .actionWithLabel(
            "",
            collectAction = { label, data ->
              _commentSubmitState.resetWithLog(label, data.toNetworkResult())
            },
            catchAction = { label, error ->
              _commentSubmitState.resetWithLog(label, networkErrorWithLog(error, "评论失败，稍后再试"))
            },
          )
      }
    }
  }

  fun navigateToReport(type: ReportType) {
    rootAction.navigateFormPostToReport(type)
  }

  /**
   * 获取帖子的评论树
   *
   * @param treeStart String 树的开始
   * @param postId String
   */
  fun getPostCommentTree(treeStart: String, postId: String) {
    viewModelScope.launch {
      _postCommentTreeFlow.value =
        Pager(PagingConfig(pageSize = 10, prefetchDistance = 2)) {
          EasyPageSourceForCommentTree(
            backend =
              LoadPageDataForCommentTree {
                return@LoadPageDataForCommentTree client
                  .get("post/commentList/page/${it}/${treeStart}/${postId}")
                  .body<PostCommentTree>()
                  .data
              }
          )
        }
    }
  }

  /**
   * 帖子点赞
   *
   * @param postId Int
   */
  fun postLikes(postId: Int) {
    viewModelScope.launchInDefault {
      _postLikeSubmitState.logicIfNotLoading {
        postRepository
          .postLike(postId)
          .actionWithLabel(
            "postLike/postLike",
            catchAction = { label, error ->
              _postLikeSubmitState.resetWithLog(label, networkErrorWithLog(error, "点赞失败"))
            },
            collectAction = { label, data ->
              _postLikeSubmitState.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }
}
