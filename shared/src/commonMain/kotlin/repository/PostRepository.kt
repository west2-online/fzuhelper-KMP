package repository

import data.person.UserLabel.UserLabel
import data.post.PostById.PostById
import data.post.PostCommentNew.PostCommentNew
import data.post.PostCommentPreview.PostCommentPreview
import data.post.PostLikes.PostLikes
import data.post.PostList.PostList
import data.post.PostNew.NewPostResponse
import doist.x.normalize.Form
import doist.x.normalize.normalize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ui.compose.Release.ReleasePageItem

class PostRepository(private val client: HttpClient) {

    fun newPost(
        releasePageItemList: List<ReleasePageItem>,
        title: String,
        labelList: List<Int>
    ): Flow<NewPostResponse> {
        return flow {
            val response = client.post("/post/new") {
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            releasePageItemList.forEachIndexed{ index,item->
                                when (item) {
                                    is ReleasePageItem.ImageItem -> {
                                        append("image",item.image.value!!,
                                            Headers.build {
                                                append("order", index.toString())
                                                append(HttpHeaders.ContentType, "image/png")
                                                append(HttpHeaders.ContentDisposition, "filename=${index}")
                                            }
                                        )
                                    }
                                    is ReleasePageItem.TextItem -> {
                                        append( "text","{\"order\":${index},\"value\":\"${item.text.value}\"}",
                                            Headers.build {
                                                append("Content-Type", "text/plain")
                                            })
                                    }
                                    is ReleasePageItem.LineChartItem -> {

                                    }
                                }
                            }
                            append("title",title.normalize(Form.NFKD))
                            labelList.forEach {
                                append("label",it)
                            }
                        },
                        boundary = "WebAppBoundary"
                    )
                )
            }.body<NewPostResponse>()
            emit(response)
        }
    }

    suspend fun getUserLabel():Flow<UserLabel>{
        return flow {
            val userLabelList = client.get("/user/label").body<UserLabel>()
            emit(userLabelList)
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
                                        append(HttpHeaders.ContentType, "image/png")
                                        append(HttpHeaders.ContentDisposition, "filename=contentImage")
                                    }
                                )
//                                append("parentId", parentId.toString())
//                                append("postId", postId.toString())
//                                append("tree", tree)
                            }
                            append("content", content.normalize(Form.NFD))
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

    fun postLike(postId: Int): Flow<PostLikes> {
        return flow<PostLikes> {
            val response = client.submitForm(
                url = "/post/like",
                formParameters = parameters {
                append("postId",postId.toString())
            }).body<PostLikes>()
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


