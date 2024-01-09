package repository

import data.Report.reportData.ReportResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ReportRepository (private val client: HttpClient){

    fun reportComment(commentId:String,typeId:Int,postId:String): Flow<ReportResponse> {
        return flow {
            val response = client.post(
                "/report/comment",
            ){
                setBody{
                    formData {
                        append("postId",postId)
                        append("commentId",commentId)
                        append("typeId",typeId)
                    }
                }
            }.body<ReportResponse>()
            emit(response)
        }
    }

    fun reportPost(typeId:Int,postId:String): Flow<ReportResponse> {
        return flow {
            val response = client.post(
                "/report/comment",
            ){
                setBody{
                    formData {
                        append("postId",postId)
                        append("typeId",typeId)
                    }
                }
            }.body<ReportResponse>()
            emit(response)
        }
    }

}

enum class ReportStatus(val value: Int, val description: String) {
    InsufficientInformation(0, "信息不足"),
    TheReportFailed(1, "报告失败"),
    TheReportWasSuccessful(2, "报告成功")
}