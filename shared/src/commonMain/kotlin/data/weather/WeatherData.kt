package data.weather

import kotlinx.serialization.Serializable

@Serializable
data class WeatherData(
  val cityInfo: CityInfo,
  val `data`: Data,
  val date: String,
  val message: String,
  val status: Int,
  val time: String,
)
