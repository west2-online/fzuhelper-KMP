package data.post.PostCommentTree

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val FirstImage: String,
    val Id: Int,
    val LikeNum: Int,
    val LittleDescribe: String,
    val status: Int,
    val Time: String,
    val Title: String,
    val User: UserX,
    val UserId: Int
)