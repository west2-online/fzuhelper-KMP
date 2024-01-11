package data.Person.identity

import kotlinx.serialization.Serializable

@Serializable
data class PersonIdentityData(
    val code: Int,
    val `data`: List<Data>?,
    val msg: String
)