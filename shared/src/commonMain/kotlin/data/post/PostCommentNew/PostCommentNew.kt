package data.post.PostCommentNew

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentNew(
    val code: Int,
    val `data`: String?,
    val msg: String?
)