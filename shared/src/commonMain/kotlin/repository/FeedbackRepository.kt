package repository

import data.feedback.FeedbackDetailComment.FeedbackDetailComment
import data.feedback.FeelbackDetail.FeedbackDetail
import data.feedback.SubmitNewFeedBack.FeedbackSubmit
import doist.x.normalize.Form
import doist.x.normalize.normalize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.http.Parameters
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeedbackRepository(val client: HttpClient) {

    fun submitNewFeedBack(content :String,type:Int):Flow<FeedbackSubmit>{
        return flow {
            val response = client.submitForm(
                "/feedback/new",
                formParameters = Parameters.build {
                    append("Content",content.normalize(Form.NFKD))
                    append("Type",type.toString())
                }
            ).body<FeedbackSubmit>()
            emit(response)
        }
    }

    fun getFeedbackDetail(id:Int):Flow<FeedbackDetail>{
        return flow {
            val response = client.get("/feedback/detail/${id}").body<FeedbackDetail>()
            emit(response)
        }
    }

    fun postFeedbackDetailComment(comment : String,id:Int ):Flow<FeedbackDetailComment>{
        return flow {
            val response = client.submitForm(
                url = "/feedback/comment",
                formParameters = parameters {
                    append("Comment",comment.normalize(Form.NFKD))
                    append("Id",id.toString())
                }
            ).body<FeedbackDetailComment>()
            emit(response)
        }
    }


}