package data.post

import kotlinx.serialization.Serializable

@Serializable
data class NewPostResponse(
    val code: Int,
    val `data`: String?,
    val msg: String?
)

