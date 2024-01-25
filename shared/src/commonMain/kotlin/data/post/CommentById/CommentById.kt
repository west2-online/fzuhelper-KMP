package data.post.CommentById

import data.share.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentById(
    val code: Int,
    val `data`: Comment,
    val msg: String
)