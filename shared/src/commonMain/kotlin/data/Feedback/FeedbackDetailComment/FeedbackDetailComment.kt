package data.Feedback.FeedbackDetailComment

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackDetailComment(
    val code: Int,
    val `data`: String?,
    val msg: String
)