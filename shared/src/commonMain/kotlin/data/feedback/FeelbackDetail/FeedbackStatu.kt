package data.feedback.FeelbackDetail

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackStatu(
    val Feedback: Feedback,
    val Feedback_Id: Int,
    val Id: Int,
    val Message: String,
    override val Order: Int,
    val Status: Int,
    val Time: String,
    val User: User,
    val User_Id: Int
):FeedbackItem