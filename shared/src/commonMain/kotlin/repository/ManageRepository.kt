package repository

import data.Manage.processPost.ProcessPost
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ManageRepository(
    val client: HttpClient
) {
    fun processPost(postId:Int, status: Int): Flow<ProcessPost> {
        return flow<ProcessPost> {
            val result = client.submitForm(
                "/manage/post/process",
                formParameters = parameters{
                    append("postId",postId.toString())
                    append("status",status.toString())
                }
            ).body<ProcessPost>()
            emit(result)
        }

    }
}