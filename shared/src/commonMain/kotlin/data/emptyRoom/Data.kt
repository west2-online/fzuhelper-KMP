package data.emptyRoom

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val EmptyRoomList: Map<String,List<EmptyItemData>?>?
)