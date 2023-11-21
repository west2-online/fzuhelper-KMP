package repository

import data.post.NewPostResponse
import data.post.PostById.PostById
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
                                        append( index.toString(),item.text.value,
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
                headers.append("title",title)
            }.body<NewPostResponse>()
            emit(response)
        }
    }
    fun getPostById(id:String): Flow<PostById> {
        return flow {
            val response = client.get("/post/post/${id}").body<PostById>()
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

