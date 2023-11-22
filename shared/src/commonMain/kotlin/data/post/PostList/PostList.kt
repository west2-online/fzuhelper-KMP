package data.post.PostList

import kotlinx.serialization.Serializable

@Serializable
data class PostList(
    val code: Int,
    val `data`: List<Data>?,
    val msg: String
)