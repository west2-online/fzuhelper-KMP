package data.Feedback.FeedbackList

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val Id: Int,
    val Identify: Int,
    val age: Int,
    val avatar: String,
    val email: String,
    val gender: String,
    val location: String,
    val username: String
)