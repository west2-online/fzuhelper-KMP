package data.feedback.github.githubIssueListByPage

import kotlinx.serialization.Serializable
import util.regex.toGithubAvatar

@Serializable()
data class GithubIssueByPageItem(
  val active_lock_reason: String? = null,
  val assignee: String? = null,
  val assignees: List<String?>? = null,
  val author_association: String? = null,
  val body: String? = null,
  val closed_at: String? = null,
  val comments: Int = 0,
  val comments_url: String = "",
  val created_at: String = "",
  val events_url: String = "",
  val html_url: String,
  val id: Long,
  val labels: List<Label>,
  val labels_url: String,
  val locked: Boolean,
  val milestone: String?,
  val node_id: String,
  val number: Int,
  val performed_via_github_app: String?,
  val reactions: Reactions,
  val repository_url: String,
  val state: String,
  val state_reason: String?,
  val timeline_url: String,
  val title: String,
  val updated_at: String,
  val url: String,
  val user: User,
) {
  fun toAvatar(): String {
    return toGithubAvatar(this.user.login ?: "", user.avatar_url ?: "", content = body ?: "")
  }
}
