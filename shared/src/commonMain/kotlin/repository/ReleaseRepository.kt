package repository

import data.post.NewPostResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import ui.compose.Release.ReleasePageItem

class ReleaseRepository(private val client: HttpClient) {
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
    fun getPostById(releasePageItemList:List<ReleasePageItem>,title : String): Flow<NewPostResponse> {
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
}