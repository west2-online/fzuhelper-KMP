package data.feedback.github.githubComment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubCommentsItem(
  @SerialName("body") val body: String?,
  @SerialName("created_at") val createdAt: String?,
  @SerialName("id") val id: Long?,
  @SerialName("issue_url") val issueUrl: String?,
  @SerialName("reactions") val reactions: Reactions?,
  @SerialName("updated_at") val updatedAt: String?,
  @SerialName("url") val url: String?,
  @SerialName("user") val user: User?,
)
