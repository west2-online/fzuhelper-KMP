package data.ribbon

import kotlinx.serialization.Serializable

@Serializable
data class RibbonData(
    val Action: String,
    val Id: Int
)