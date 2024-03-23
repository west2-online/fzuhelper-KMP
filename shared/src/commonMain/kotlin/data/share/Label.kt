package data.share

import kotlinx.serialization.Serializable

@Serializable
data class Label(
    val Id: Int,
    val Label: String
)