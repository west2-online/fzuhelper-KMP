package data.Feedback.FeelbackDetail

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackComment(
    val Comment: String,
    val Feedback: Feedback,
    val Feedback_Id: Int,
    val Id: Int,
    override val Order: Int,
    val Status: String,
    val User: User,
    val User_Id: Int,
    val Time:String
):FeedbackDetailItem