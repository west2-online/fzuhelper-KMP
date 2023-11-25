package data.Feedback.FeedbackList

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val Id: Int,
    val Status: Int,
    val Tab: String,
    val Time: String,
    val Type: Int,
    val User: User,
    val User_Id: Int
)