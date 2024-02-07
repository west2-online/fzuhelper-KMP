package data.manage.openImageAdd

import kotlinx.serialization.Serializable

@Serializable
data class OpenImageAdd(
    val code: Int,
    val `data`: String?,
    val msg: String
)