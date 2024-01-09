package ui.compose.Post

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import data.post.PostById.PostById
import data.post.PostCommentPreview.Data
import data.post.PostCommentPreview.PostCommentPreview
import data.post.PostCommentTree.PostCommentTree
import data.post.share.Comment
import data.post.share.Post
import data.post.share.User
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
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import repository.PostRepository
import ui.compose.Report.ReportType
import ui.root.RootAction
import ui.root.RootTarget
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.flow.launchInDefault
import ui.util.network.NetworkResult
import ui.util.network.loginIfNotLoading
import ui.util.network.reset

class PostDetailViewModel(
    private val client: HttpClient,
    private val postRepository : PostRepository,
    private val rootAction: RootAction
):ViewModel() {

    private val _currentPostDetail = CMutableStateFlow(MutableStateFlow<NetworkResult<PostById>>(NetworkResult.UnSend()))
    val currentPostDetail = _currentPostDetail.asStateFlow()

    private val _postCommentPreviewFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, Data>?>(null))
    var postCommentPreviewFlow = _postCommentPreviewFlow.asStateFlow()

    private val _postCommentTreeFlow = CMutableStateFlow(MutableStateFlow<Pager<Int, data.post.PostCommentTree.Data>?>(null))
    var postCommentTreeFlow = _postCommentTreeFlow.asStateFlow()

    private val _commentSubmitState = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val commentSubmitState = _commentSubmitState.asStateFlow()

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
                        println("list")
                        try {
                            val postCommentPreview = client.get("post/comment/page/${it}/${postId}").body<PostCommentPreview>()
                            println("sss${Json.encodeToString(postCommentPreview)}")
                            return@LoadPageDataForCommentPreview postCommentPreview.data
                        }
                        catch (e:Exception){
                            println(e.message.toString())
                        }
                        return@LoadPageDataForCommentPreview listOf(
                            Data(
                                MainComment = Comment(
                                    Content = "null",
                                    Id = 6903,
                                    Image = "null",
                                    ParentId = 6910,
                                    Post = Post(
                                        FirstImage = "Tobie",
                                        Id = 2220,
                                        LikeNum = 5609,
                                        LittleDescribe = "Atthew",
                                        Status = 3372,
                                        Time = "2024-01-09T14:35:37.725+08:00",
                                        Title = "Amira",
                                        User = User(
                                            Id = 5478,
                                            Identify = 5425,
                                            age = 2177,
                                            avatar = "null",
                                            email = "null",
                                            gender = "null",
                                            location = "null",
                                            username = "null",
                                        ),
                                        UserId = 7208
                                    ),
                                    PostId = 8752,
                                    Time = "2024-01-09T14:35:37.725+08:00",
                                    Tree = "null",
                                    User = User(
                                        Id = 0,
                                        Identify = 0,
                                        age = 0,
                                        avatar = "",
                                        email = "",
                                        gender = "",
                                        location = "",
                                        username = ""
                                    ),
                                    UserId = 1309,
                                    Status = 0
                                ), SonComment = listOf()

                            )
                        )

                    }
                )
            }
        }
    }

    fun getPostById(id: String){
        viewModelScope.launch (Dispatchers.IO){
            _currentPostDetail.reset(NetworkResult.Loading())
            postRepository.getPostById(id = id)
                .catchWithMassage {
                    _currentPostDetail.reset(NetworkResult.Error(Throwable("帖子获取失败")))
                }
                .collectWithMassage{
                    _currentPostDetail.reset(NetworkResult.Success(it))
                }
        }
    }

    fun submitComment(parentId:Int,postId:Int,tree:String,content:String,image:ByteArray?){
        viewModelScope.launchInDefault {
            _commentSubmitState.loginIfNotLoading {
                postRepository.postNewComment(parentId,postId,tree,content.normalize(Form.NFC),image)
                    .catchWithMassage {
                        _commentSubmitState.reset(NetworkResult.Error(Throwable("评论失败，稍后再试")))
                    }.collectWithMassage{
                        _commentSubmitState.reset(it.toNetworkResult())
                    }
            }
        }
    }

    fun navigateToRelease(){ rootAction.navigateToNewTarget(RootTarget.Release) }

    fun navigateToReport(type: ReportType){
        rootAction.navigateToNewTarget(RootTarget.Report(type))
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

}