package data.feedback.FeelbackDetail

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackDetail(
    val code: Int,
    val `data`: Data,
    val msg: String
)