package data.emptyRoom

import kotlinx.serialization.Serializable

@Serializable
data class EmptyRoom(
    val Name: String,
    val Number: String,
    val RoomType: String
)