package data.feedback.FeelbackDetail

import data.share.User
import kotlinx.serialization.Serializable

@Serializable
data class Feedback(
    val Id: Int,
    val Status: Int,
    val Tab: String,
    val Time: String,
    val Type: Int,
    val User: User,
    val User_Id: Int
)