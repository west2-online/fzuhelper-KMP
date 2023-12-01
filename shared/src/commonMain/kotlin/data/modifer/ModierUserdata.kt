package data.modifer

import kotlinx.serialization.Serializable

@Serializable
data class ModifierData(
    val code: Int,
    val `data`: String?,
    val msg: String?
)

@Serializable
data class ModifierAvatar(
    val code: Int,
    val `data`: String?,
    val msg: String?
)