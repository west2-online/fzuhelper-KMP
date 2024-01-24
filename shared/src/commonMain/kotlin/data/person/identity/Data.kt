package data.person.identity

import kotlinx.serialization.Serializable


@Serializable
data class Data(
    val Id: Int,
    val Identity: String
)