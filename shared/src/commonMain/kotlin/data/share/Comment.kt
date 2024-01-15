package data.share

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
    val Content: String,
    val Id: Int,
    val Image: String,
    val ParentId: Int,
    val Post: Post,
    val PostId: Int,
    val Status: Int,
    val Time: String,
    val Tree: String,
    val User: User,
    val UserId: Int
)