package data.feedback.github.githubComment


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Reactions(
    @SerialName("confused")
    val confused: Long?,
    @SerialName("eyes")
    val eyes: Long?,
    @SerialName("heart")
    val heart: Long?,
    @SerialName("hooray")
    val hooray: Long?,
    @SerialName("laugh")
    val laugh: Long?,
    @SerialName("rocket")
    val rocket: Long?,
    @SerialName("total_count")
    val totalCount: Long?,
    @SerialName("url")
    val url: String?,

)