package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class PostById(
    val code: Int,
    val `data`: Data,
    val msg: String
)