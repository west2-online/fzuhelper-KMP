package data.Person

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val age: Int,
    val email: String,
    val gender: String,
    val location: String,
    val username: String
)