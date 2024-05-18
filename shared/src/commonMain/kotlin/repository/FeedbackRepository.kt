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

/**
 * 反馈的仓库层
 * @property client HttpClient
 * @constructor
 */
class FeedbackRepository(val client: HttpClient) {
    /**
     * 创建新的反馈
     * @param content String 反馈内容
     * @param type Int 反馈类型
     * @return Flow<FeedbackSubmit>
     */
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

    /**
     * 获取反馈的详情
     * @param id Int 反馈详情的id
     * @return Flow<FeedbackDetail>
     */
    fun getFeedbackDetail(id:Int):Flow<FeedbackDetail>{
        return flow {
            val response = client.get("/feedback/detail/${id}").body<FeedbackDetail>()
            emit(response)
        }
    }

    /**
     * 对特定的反馈发表评论
     * @param comment String
     * @param id Int
     * @return Flow<FeedbackDetailComment>
     */
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