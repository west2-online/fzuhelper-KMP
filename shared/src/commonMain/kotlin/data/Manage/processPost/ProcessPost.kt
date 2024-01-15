package data.Manage.processPost

import kotlinx.serialization.Serializable

@Serializable
data class ProcessPost(
    val code: Int,
    val `data`: String?,
    val msg: String
)