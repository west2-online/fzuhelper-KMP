package data.Feedback.FeelbackDetail

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackStatus(
    val Feedback: Feedback,
    val Feedback_Id: Int,
    val Id: Int,
    val Message: String,
    override val Order: Int,
    val Status: Int,
    val User: User,
    val User_Id: Int,
    val Time:String
):FeedbackDetailItem

interface FeedbackDetailItem{
    val Order: Int
}