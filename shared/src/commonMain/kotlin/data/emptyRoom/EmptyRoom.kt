package data.emptyRoom

import kotlinx.serialization.Serializable

@Serializable
data class EmptyRoom(
  val build: String,
  val location: String,
  val capacity: String,
  val type: String
)
