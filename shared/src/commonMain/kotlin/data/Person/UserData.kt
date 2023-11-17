package data.Person

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val code: Int,
    val `data`: Data?,
    val msg: String
)