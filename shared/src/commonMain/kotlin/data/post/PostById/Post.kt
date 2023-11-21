package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val Id: Int,
    val Statue: Int,
    val Time: String,
    val Title: String,
    val User: User,
    val UserId: Int
)