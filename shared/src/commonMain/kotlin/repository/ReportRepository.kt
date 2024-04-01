package repository

import data.Report.reportData.ReportResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReportRepository (
    private val client: HttpClient
){
    fun reportComment(commentId:String,typeId:Int,postId:String): Flow<ReportResponse> {
        return flow {
            val response = client.submitForm(
                "/report/comment",
                formParameters = Parameters.build {
                    append("postId",postId)
                    append("commentId",commentId)
                    append("typeId",typeId.toString())
                }
            ).body<ReportResponse>()
            emit(response)
        }
    }

    fun reportPost(typeId:Int,postId:String): Flow<ReportResponse> {
        return flow {
            val response = client.submitForm(
                "/report/post",
                formParameters = Parameters.build {
                    append("postId",postId)
                    append("typeId",typeId.toString())
                }
            ).body<ReportResponse>()
            emit(response)
        }
    }

}

