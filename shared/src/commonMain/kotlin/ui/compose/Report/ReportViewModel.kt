package ui.compose.Report

import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.ReportRepository
import util.flow.actionWithLabel
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 举报逻辑
 *
 * @property repository ReportRepository
 * @property _reportCommentResponse CMutableStateFlow<NetworkResult<String>>
 * @property reportCommentResponse StateFlow<NetworkResult<String>>
 * @property _reportPostResponse CMutableStateFlow<NetworkResult<String>>
 * @property reportPostResponse StateFlow<NetworkResult<String>>
 * @constructor
 */
class ReportViewModel(private val repository: ReportRepository) : ViewModel() {
  private val _reportCommentResponse =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val reportCommentResponse = _reportCommentResponse.asStateFlow()

  private val _reportPostResponse =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  val reportPostResponse = _reportPostResponse.asStateFlow()

  /**
   * 举报评论
   *
   * @param commentId String
   * @param typeId Int
   * @param postId String
   */
  fun reportComment(commentId: String, typeId: Int, postId: String) {
    viewModelScope.launchInDefault {
      repository
        .reportComment(commentId, typeId, postId)
        .actionWithLabel(
          "reportComment/reportComment",
          catchAction = { label, error ->
            _reportCommentResponse.resetWithLog(label, networkErrorWithLog(error, "举报失败"))
          },
          collectAction = { label, data ->
            _reportCommentResponse.resetWithLog(label, data.toNetworkResult())
          },
        )
    }
  }

  /**
   * 举报帖子
   *
   * @param typeId Int
   * @param postId String
   */
  fun reportPost(typeId: Int, postId: String) {
    viewModelScope.launchInDefault {
      repository
        .reportPost(typeId, postId)
        .actionWithLabel(
          "reportPost/reportPost",
          catchAction = { label, error ->
            _reportPostResponse.resetWithLog(label, networkErrorWithLog(error, "举报失败"))
          },
          collectAction = { label, data ->
            _reportPostResponse.resetWithLog(label, data.toNetworkResult())
          },
        )
    }
  }
}
