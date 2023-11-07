package repository.register.bean

import kotlinx.serialization.Serializable

@Serializable
data class AuthenticationResponse(
    val code: Int,
    val `data`: String,
    val msg: String
)