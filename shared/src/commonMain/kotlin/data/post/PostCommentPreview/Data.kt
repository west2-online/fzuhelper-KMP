package data.post.PostCommentPreview

import data.post.share.Comment
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val MainComment: Comment,
    val SonComment: List<Comment>
)