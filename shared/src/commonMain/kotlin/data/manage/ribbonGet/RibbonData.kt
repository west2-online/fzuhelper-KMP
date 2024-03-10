package data.manage.ribbonGet

import kotlinx.serialization.Serializable

@Serializable
data class RibbonData(
    val Action: String,
    val Id: Int,
    val Image: String
)