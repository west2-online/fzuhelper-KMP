package data.post.PostCommentTree

import kotlinx.serialization.Serializable

@Serializable
data class PostCommentTree(
    val code: Int,
    val `data`: List<Data>,
    val msg: String
)