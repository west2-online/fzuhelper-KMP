package ui.compose.Manage

import androidx.paging.Pager
import androidx.paging.PagingSource
import app.cash.paging.PagingConfig
import app.cash.paging.cachedIn
import data.manage.adminList.Admin
import data.manage.commentReportData.CommentReportContextData
import data.manage.commentReportData.CommentReportForResponseList
import data.manage.postReportPage.PostReportContextData
import data.manage.postReportPage.PostReportForResponseList
import data.manage.ribbonGet.RibbonData
import data.post.CommentById.CommentById
import data.post.PostById.PostById
import data.post.PostById.PostData
import data.share.Comment
import data.share.User
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.ManageRepository
import util.flow.actionWithLabel
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog
import util.network.resetWithoutLog

/**
 * 管理界面的逻辑
 *
 * @property client HttpClient
 * @property repository ManageRepository
 * @property postReportPageList Flow<PagingData<PostReportData>>
 * @property commentReportPageList Flow<PagingData<CommentReportData>>
 * @property _openImageList CMutableStateFlow<NetworkResult<List<String>>>
 * @property openImageList StateFlow<NetworkResult<List<String>>> 获取开屏页list
 * @property _openImageDelete CMutableStateFlow<NetworkResult<String>>
 * @property openImageDelete StateFlow<NetworkResult<String>> 删除开屏页的结果
 * @property _openImageAdd CMutableStateFlow<NetworkResult<String>>
 * @property openImageAdd StateFlow<NetworkResult<String>> 添加开屏页的结果
 * @property _ribbonImageAdd CMutableStateFlow<NetworkResult<String>>
 * @property ribbonImageAdd StateFlow<NetworkResult<String>> 添加轮播页的结果
 * @property _ribbonList CMutableStateFlow<NetworkResult<List<RibbonData>>>
 * @property ribbonList StateFlow<NetworkResult<List<RibbonData>>> 获取轮播页list的结果
 * @property _ribbonDelete CMutableStateFlow<NetworkResult<String>>
 * @property ribbonDelete StateFlow<NetworkResult<String>> 删除轮播页结果
 * @property _userByEmail CMutableStateFlow<NetworkResult<User>>
 * @property userByEmail StateFlow<NetworkResult<User>> 用邮箱获取用户的结果
 * @property _adminList CMutableStateFlow<NetworkResult<List<Admin>>>
 * @property adminList StateFlow<NetworkResult<List<Admin>>> 获取管理列表的结果
 * @property _adminAdd CMutableStateFlow<NetworkResult<String>>
 * @property adminAdd StateFlow<NetworkResult<String>> 添加管理员的结果
 * @property _adminLevelUpdate CMutableStateFlow<NetworkResult<String>>
 * @property adminLevelUpdate StateFlow<NetworkResult<String>> 更新管理员等级的结果
 * @constructor
 */
class ManageViewModel(val client: HttpClient, val repository: ManageRepository) : ViewModel() {
  /** 帖子举报分页数据 */
  var postReportPageList =
    Pager(PagingConfig(pageSize = 10, prefetchDistance = 2)) {
        EasyPageSourceForPostReport(
          backend =
            LoadPostReportPageData {
              val postReportDataList = mutableListOf<PostReportData>()
              val postReportData =
                client.get("/manage/post/list/${it}").body<PostReportForResponseList>()
              postReportData.data.forEach { postReportContextData ->
                val postById =
                  client.get("/post/id/${postReportContextData.Post.Id}").body<PostById>()
                postReportDataList.add(
                  PostReportData(
                    postReportContextData = postReportContextData,
                    postData = postById.data,
                  )
                )
              }
              return@LoadPostReportPageData postReportDataList.toList()
            }
        )
      }
      .flow
      .cachedIn(viewModelScope)

  /** 评论举报分页数据 */
  var commentReportPageList =
    Pager(PagingConfig(pageSize = 10, prefetchDistance = 2)) {
        EasyPageSourceForCommentReport(
          backend =
            LoadCommentReportPageData {
              try {
                val commentReportDataList = mutableListOf<CommentReportData>()
                val commentReportData =
                  client.get("/manage/comment/list/${it}").body<CommentReportForResponseList>()
                commentReportData.data.forEach { commentReportContextData ->
                  val comment =
                    client
                      .get("/post/comment/${commentReportContextData.Comment.Id}")
                      .body<CommentById>()
                  commentReportDataList.add(
                    CommentReportData(
                      commentReportContextData = commentReportContextData,
                      comment = comment.data,
                    )
                  )
                }
                return@LoadCommentReportPageData commentReportDataList.toList()
              } catch (e: Exception) {
                println("this is error${e.message}")
                return@LoadCommentReportPageData listOf()
              }
            }
        )
      }
      .flow
      .cachedIn(viewModelScope)

  private var _openImageList =
    CMutableStateFlow(MutableStateFlow<NetworkResult<List<String>>>(NetworkResult.UnSend()))
  var openImageList = _openImageList.asStateFlow()

  private var _openImageDelete =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  var openImageDelete = _openImageDelete.asStateFlow()

  private var _openImageAdd =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  var openImageAdd = _openImageAdd.asStateFlow()

  private var _ribbonImageAdd =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  var ribbonImageAdd = _ribbonImageAdd.asStateFlow()

  private var _ribbonList =
    CMutableStateFlow(MutableStateFlow<NetworkResult<List<RibbonData>>>(NetworkResult.UnSend()))
  var ribbonList = _ribbonList.asStateFlow()

  private var _ribbonDelete =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  var ribbonDelete = _ribbonDelete.asStateFlow()

  private var _userByEmail =
    CMutableStateFlow(MutableStateFlow<NetworkResult<User>>(NetworkResult.UnSend()))
  var userByEmail = _userByEmail.asStateFlow()

  private var _adminList =
    CMutableStateFlow(MutableStateFlow<NetworkResult<List<Admin>>>(NetworkResult.UnSend()))
  var adminList = _adminList.asStateFlow()

  private var _adminAdd =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  var adminAdd = _adminAdd.asStateFlow()

  private val _adminLevelUpdate =
    CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
  var adminLevelUpdate = _adminLevelUpdate.asStateFlow()

  /** Get open image 获取开屏页 */
  fun getOpenImage() {
    viewModelScope.launchInDefault {
      _openImageList.logicIfNotLoading {
        repository
          .getImageList()
          .actionWithLabel(
            "actionWithLabel/actionWithLabel",
            catchAction = { label, error ->
              _openImageList.resetWithLog(label, networkErrorWithLog(error, "获取失败"))
            },
            collectAction = { label, data ->
              _openImageList.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }

  /** Get ribbon data 获取轮播页列表 */
  fun getRibbonData() {
    viewModelScope.launchInDefault {
      _ribbonList.logicIfNotLoading {
        repository
          .getRibbonList()
          .actionWithLabel(
            "getRibbonData/getRibbonList",
            collectAction = { label, data ->
              _ribbonList.resetWithLog(label, data.toNetworkResult())
            },
            catchAction = { label, error ->
              _ribbonList.resetWithLog(label, networkErrorWithLog(error, "获取失败"))
            },
          )
      }
    }
  }

  /** Refresh 更新数据 */
  fun refresh() {
    getOpenImage()
    getRibbonData()
    refreshAdminList()
  }

  /**
   * 处理被举报的帖子
   *
   * @param reportState MutableStateFlow<NetworkResult<String>>
   * @param postId Int
   * @param result PostProcessResult
   */
  fun dealPost(
    reportState: MutableStateFlow<NetworkResult<String>>,
    postId: Int,
    result: PostProcessResult,
  ) {
    viewModelScope.launchInDefault {
      reportState.logicIfNotLoading {
        repository
          .processPost(postId, result.value)
          .actionWithLabel(
            "dealPost/processPost",
            collectAction = { label, data ->
              reportState.resetWithLog(label, data.toNetworkResult())
            },
            catchAction = { label, error ->
              reportState.resetWithLog(label, networkErrorWithLog(error, "操作失败"))
            },
          )
      }
    }
  }

  /**
   * 处理被举报的评论
   *
   * @param reportState MutableStateFlow<NetworkResult<String>>
   * @param commentId Int
   * @param postId Int
   * @param result CommentProcessResult
   */
  fun dealComment(
    reportState: MutableStateFlow<NetworkResult<String>>,
    commentId: Int,
    postId: Int,
    result: CommentProcessResult,
  ) {
    viewModelScope.launchInDefault {
      reportState.logicIfNotLoading {
        repository
          .processComment(commentId, postId, result.value)
          .actionWithLabel(
            "dealPost/processComment",
            collectAction = { label, data ->
              reportState.resetWithLog(label, data.toNetworkResult())
            },
            catchAction = { label, error ->
              reportState.resetWithLog(label, networkErrorWithLog(error, "操作失败"))
            },
          )
      }
    }
  }

  /**
   * 删除开屏页
   *
   * @param openImageName String
   */
  fun deleteOpenImage(openImageName: String) {
    viewModelScope.launchInDefault {
      _openImageDelete.logicIfNotLoading {
        repository
          .deleteOpenImage(openImageName)
          .actionWithLabel(
            "dealPost/processComment",
            collectAction = { label, data ->
              _openImageDelete.resetWithLog(label, data.toNetworkResult())
              refresh()
            },
            catchAction = { label, error ->
              _openImageDelete.resetWithLog(label, networkErrorWithLog(error, "删除失败"))
              refresh()
            },
          )
      }
    }
  }

  /**
   * 添加新的开屏页
   *
   * @param openImage ByteArray
   */
  fun addOpenImage(openImage: ByteArray) {
    viewModelScope.launchInDefault {
      _openImageAdd.logicIfNotLoading {
        repository
          .addNewOpenImage(openImage)
          .actionWithLabel(
            "dealPost/processComment",
            collectAction = { label, data ->
              _openImageAdd.resetWithLog(label, data.toNetworkResult())
              refresh()
            },
            catchAction = { label, error ->
              _openImageAdd.resetWithLog(label, networkErrorWithLog(error, "添加失败"))
              refresh()
            },
          )
      }
    }
  }

  /**
   * 添加新的开轮播图
   *
   * @param ribbonImage ByteArray
   * @param ribbonAction String
   */
  fun addRibbonImage(ribbonImage: ByteArray, ribbonAction: String) {
    viewModelScope.launchInDefault {
      _ribbonImageAdd.logicIfNotLoading {
        repository
          .addNewRibbonImage(ribbonImage, ribbonAction)
          .actionWithLabel(
            "addRibbonImage/addNewRibbonImage",
            collectAction = { label, data ->
              _ribbonImageAdd.resetWithLog(label, data.toNetworkResult())
              refresh()
            },
            catchAction = { label, error ->
              _ribbonImageAdd.resetWithLog(label, networkErrorWithLog(error, "添加失败"))
              refresh()
            },
          )
      }
    }
  }

  /**
   * 删除轮播页
   *
   * @param imageName String
   */
  fun deleteRibbon(imageName: String) {
    viewModelScope.launchInDefault {
      _ribbonDelete.logicIfNotLoading(
        preAction = { _ribbonList.resetWithoutLog(NetworkResult.UnSend()) }
      ) {
        repository
          .deleteRibbon(imageName = imageName)
          .actionWithLabel(
            "deleteRibbon/deleteRibbon",
            collectAction = { label, data ->
              _ribbonDelete.resetWithLog(label, data.toNetworkResult())
              refresh()
            },
            catchAction = { label, error ->
              _ribbonDelete.resetWithLog(label, networkErrorWithLog(error, "删除失败"))
              refresh()
            },
          )
      }
    }
  }

  /**
   * 通过email获取user的信息
   *
   * @param email String
   */
  fun getUserDataByEmail(email: String) {
    viewModelScope.launchInDefault {
      _userByEmail.logicIfNotLoading {
        repository
          .getUserByEmail(email)
          .actionWithLabel(
            label = "getUserDataByEmail/getEmailByEmail",
            catchAction = { label, error ->
              _userByEmail.resetWithLog(label, networkErrorWithLog(error, "用户信息获取失败"))
            },
            collectAction = { label, data ->
              _userByEmail.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }

  /** Refresh admin list 更新管理员列表 */
  fun refreshAdminList() {
    viewModelScope.launchInDefault {
      _adminList.logicIfNotLoading {
        repository
          .getAdminList()
          .actionWithLabel(
            "refreshAdminList/getAdminList",
            collectAction = { label, data ->
              _adminList.resetWithLog(label, data.toNetworkResult())
            },
            catchAction = { label, error ->
              _adminList.resetWithLog(label, networkErrorWithLog(error, "获取失败"))
            },
          )
      }
    }
  }

  /**
   * 添加管理员界面
   *
   * @param email String
   */
  fun addManager(email: String) {
    viewModelScope.launchInDefault {
      _adminAdd.logicIfNotLoading {
        repository
          .addAdmin(email)
          .actionWithLabel(
            "addManager/addAdmin",
            catchAction = { label, error ->
              _adminAdd.resetWithLog(label, networkErrorWithLog(error, "添加失败"))
            },
            collectAction = { label, data -> _adminAdd.resetWithLog(label, data.toNetworkResult()) },
          )
      }
    }
  }

  /**
   * 更新管理员等级
   *
   * @param userId Int
   * @param level Int
   */
  fun adminLevelUpdate(userId: Int, level: Int) {
    viewModelScope.launchInDefault {
      _adminLevelUpdate.logicIfNotLoading {
        repository
          .updateAdminLevel(level, userId)
          .actionWithLabel(
            label = "adminLevelUpdate/updateAdminLevel",
            catchAction = { label, error ->
              _adminLevelUpdate.resetWithLog(label, networkErrorWithLog(error, "更新失败"))
            },
            collectAction = { label, data ->
              _adminLevelUpdate.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }
}

/**
 * 帖子处理结果
 *
 * @property value Int
 * @constructor
 */
enum class PostProcessResult(val value: Int) {
  BanPost(2),
  PassPost(0),
}

/**
 * 评论处理结果
 *
 * @property value Int
 * @constructor
 */
enum class CommentProcessResult(val value: Int) {
  BanComment(2),
  PassComment(0),
}

class PostReportData(
  val postReportContextData: PostReportContextData,
  val postData: PostData,
  val state: MutableStateFlow<NetworkResult<String>> =
    MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()),
)

class CommentReportData(
  val commentReportContextData: CommentReportContextData,
  val comment: Comment,
  val state: MutableStateFlow<NetworkResult<String>> =
    MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()),
)

data class PageLoadDataForPostReport(val result: List<PostReportData>?, val nextPageNumber: Int?)

class EasyPageSourceForPostReport(private val backend: LoadPostReportPageData) :
  PagingSource<Int, PostReportData>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PostReportData> {
    return try {
      val page = params.key ?: 1
      val response = backend.searchPostReport(page)
      LoadResult.Page(
        data = response.result!!,
        prevKey = null, // Only paging forward.
        nextKey = response.nextPageNumber,
      )
    } catch (e: Exception) {
      LoadResult.Error(Throwable("加载失败"))
    }
  }

  override fun getRefreshKey(state: androidx.paging.PagingState<Int, PostReportData>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
  }
}

class LoadPostReportPageData(val getResult: suspend (page: Int) -> List<PostReportData>?) {
  suspend fun searchPostReport(page: Int): PageLoadDataForPostReport {
    val response = getResult(page)
    return PageLoadDataForPostReport(
      response,
      when {
        response!!.isEmpty() -> null
        response.size < 10 -> null
        else -> (page + 1)
      },
    )
  }
}

data class PageLoadDataForCommentReport(
  val result: List<CommentReportData>?,
  val nextPageNumber: Int?,
)

class EasyPageSourceForCommentReport(private val backend: LoadCommentReportPageData) :
  PagingSource<Int, CommentReportData>() {
  override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentReportData> {
    return try {
      val page = params.key ?: 1
      val response = backend.searchPostReport(page)
      LoadResult.Page(
        data = response.result!!,
        prevKey = null, // Only paging forward.
        nextKey = response.nextPageNumber,
      )
    } catch (e: Exception) {
      LoadResult.Error(Throwable("加载失败"))
    }
  }

  override fun getRefreshKey(state: androidx.paging.PagingState<Int, CommentReportData>): Int? {
    return state.anchorPosition?.let { anchorPosition ->
      val anchorPage = state.closestPageToPosition(anchorPosition)
      anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
    }
  }
}

class LoadCommentReportPageData(val getResult: suspend (page: Int) -> List<CommentReportData>?) {
  suspend fun searchPostReport(page: Int): PageLoadDataForCommentReport {
    val response = getResult(page)
    return PageLoadDataForCommentReport(
      response,
      when {
        response!!.isEmpty() -> null
        response.size < 10 -> null
        else -> (page + 1)
      },
    )
  }
}
