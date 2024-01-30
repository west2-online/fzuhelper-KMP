package data.manage.openImageList

import kotlinx.serialization.Serializable

@Serializable
data class OpenImageList(
    val code: Int,
    val `data`: List<String>,
    val msg: String
)