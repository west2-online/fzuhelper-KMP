package data.Feedback.SubmitNewFeedBack

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackSubmit(
    val code: Int,
    val `data`: String?,
    val msg: String?
)