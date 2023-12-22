package data.Person.UserData

import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val code: Int,
    val `data`: Data?,
    val msg: String
)