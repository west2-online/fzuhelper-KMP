package data.feedback.FeelbackDetail

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val Feedback: Feedback,
    val FeedbackComment: List<FeedbackComment>,
    val FeedbackStatus: List<FeedbackStatu>
)

interface FeedbackItem{
    val Order :Int
}

