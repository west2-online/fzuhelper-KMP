package data.weather

import kotlinx.serialization.Serializable

@Serializable
data class Forecast(
  val aqi: Int,
  val date: String,
  val fl: String,
  val fx: String,
  val high: String,
  val low: String,
  val notice: String,
  val sunrise: String,
  val sunset: String,
  val type: String,
  val week: String,
  val ymd: String,
)
