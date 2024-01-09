package data.post.PostList

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val FirstImage: String?,
    val Id: Int,
    val LikeNum: Int,
    val LittleDescribe: String?,
    val Status: Int,
    val Time: String,
    val Title: String,
    val User: User,
    val UserId: Int
)


