package data.post.PostComment

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentListPreview(
    val code: Int,
    val `data`: List<Data>,
    val msg: String
)