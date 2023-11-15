package data.splash

import kotlinx.serialization.Serializable

@Serializable
data class Splash(
    val code: Int,
    val `data`: String?,
    val msg: String?
)