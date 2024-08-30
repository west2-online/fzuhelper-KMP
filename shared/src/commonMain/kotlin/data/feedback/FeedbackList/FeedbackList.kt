package data.feedback.FeedbackList

import kotlinx.serialization.Serializable

@Serializable data class FeedbackList(val code: Int, val `data`: List<Data>, val msg: String)
