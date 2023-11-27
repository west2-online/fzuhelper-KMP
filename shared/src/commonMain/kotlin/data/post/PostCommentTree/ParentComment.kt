package data.post.PostCommentTree

import kotlinx.serialization.Serializable

@Serializable
data class ParentComment(
    val Content: String,
    val Id: Int,
    val Image: String,
    val ParentId: Int,
    val Post: Post,
    val PostId: Int,
    val Time: String,
    val Tree: String,
    val User: UserX,
    val UserId: Int
)