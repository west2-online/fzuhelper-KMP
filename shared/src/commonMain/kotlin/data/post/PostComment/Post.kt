package data.post.PostComment

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val FirstImage: String,
    val Id: Int,
    val LikeNum: Int,
    val LittleDescribe: String,
    val Statue: Int,
    val Time: String,
    val Title: String,
    val User: UserX,
    val UserId: Int
)