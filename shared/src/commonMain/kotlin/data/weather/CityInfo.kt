package data.weather

import kotlinx.serialization.Serializable

@Serializable
data class CityInfo(
    val city: String,
    val citykey: String,
    val parent: String,
    val updateTime: String
)