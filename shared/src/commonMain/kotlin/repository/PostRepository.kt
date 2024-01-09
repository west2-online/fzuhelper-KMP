package repository

import data.post.NewPostResponse
import data.post.PostById.PostById
import data.post.PostCommentNew.PostCommentNew
import data.post.PostCommentPreview.PostCommentPreview
import data.post.PostList.PostList
import doist.x.normalize.Form
import doist.x.normalize.normalize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ui.compose.Release.ReleasePageItem

class PostRepository(private val client: HttpClient) {

    fun newPost(releasePageItemList:List<ReleasePageItem>,title : String): Flow<NewPostResponse> {
        return flow {
            val response = client.post("/post/new") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            releasePageItemList.forEachIndexed{ index,item->
                                when (item) {
                                    is ReleasePageItem.ImageItem -> {
                                        append(index.toString(),item.image.value!!,
                                            Headers.build {
                                                append("order", index.toString())
                                                append("isImage", "true")
                                                append("Content-Disposition", "form-data; name=\"file\"; filename=\"${index}\"")
                                            }
                                        )
                                    }

                                    is ReleasePageItem.TextItem -> {
                                        append( index.toString(),item.text.value.normalize(Form.NFKD),
                                            Headers.build {
                                                append("order", index.toString())
                                                append("Content-Type", "text/plain")
                                            })
                                    }
                                }
                            }
                        },
                        boundary = "WebAppBoundary"
                    )
                )
                headers.append("title",title.normalize(Form.NFKD))
            }.body<NewPostResponse>()
            emit(response)
        }
    }

    fun getPostById(id:String): Flow<PostById> {
        return flow {
            val response = client.get("/post/id/${id}").body<PostById>()
            emit(response)
        }
    }
    fun getPostByPage(page:String): Flow<PostList> {
        return flow {
            val response = client.get("/post/page/${page}").body<PostList>()
            emit(response)
        }
    }
    fun postNewComment(parentId:Int,postId:Int,tree:String,content:String,image:ByteArray?):Flow<PostCommentNew>{
        return flow {
            val response = client.post("/post/comment/new"){
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            image?.let {
                                append(
                                    "commentImage",it,
                                    Headers.build {
                                        append("isImage", "true")
                                    }
                                )
//                                append("parentId", parentId.toString())
//                                append("postId", postId.toString())
//                                append("tree", tree)
                            }
                            append("content", content.normalize(Form.NFD),Headers.build {
                                append("isContent","ssss")
                            })
                        }
                    )
                )
                headers.append("parentId", parentId.toString())
                headers.append("postId", postId.toString())
                headers.append("tree", tree)
            }.body<PostCommentNew>()
            emit(response)
        }
    }

    fun getCommentPreview(page:Int,postId: Int):Flow<PostCommentPreview>{
        return flow {
            val response = client.get("/post/comment/page/${page}/${postId}").body<PostCommentPreview>()
            emit(response)
        }
    }
}
enum class PostStatus(val value: Int, val translation: String) {
    MissingTitleInPost(0, "在帖子中缺少标题"),
    PostInitializationFailedInPost(1, "帖子初始化失败"),
    PartParsingFailedInPost(2, "在帖子中解析部分失败"),
    FailedToSaveData(3, "保存数据失败"),
    ThePostWasPublishedSuccessfullyInPost(4, "帖子成功发布"),
    MissingIDInPost(5, "在帖子中缺少ID"),
    PostFetchFailedInPost(6, "获取帖子失败"),
    ThePostWasSuccessfulInPost(7, "获取帖子成功");
}

enum class CommentSubmitStatus(val value: Int, val description: String) {
    MissingDataWhenSubmittingAComment(0, "提交评论时缺少数据"),
    FileParsingFailed(1, "文件解析失败"),
    FailedToSaveTheCommentImage(2, "保存评论图片失败"),
    CommentFailed(3, "评论失败"),
    TheReviewWasSuccessful(4, "评论成功"),
    TheCommentIsEmpty(5, "评论为空")
}
