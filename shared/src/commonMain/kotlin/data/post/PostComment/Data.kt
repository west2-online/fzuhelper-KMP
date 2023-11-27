package data.post.PostComment

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val MainComment: MainComment,
    val SonComment: List<SonComment>
)