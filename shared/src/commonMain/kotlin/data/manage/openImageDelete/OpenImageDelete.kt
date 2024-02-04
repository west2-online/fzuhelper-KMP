package data.manage.openImageDelete

import kotlinx.serialization.Serializable

@Serializable
data class OpenImageDelete(
    val code: Int,
    val `data`: String?,
    val msg: String
)