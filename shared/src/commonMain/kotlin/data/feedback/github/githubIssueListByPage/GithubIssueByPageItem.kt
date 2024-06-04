package data.feedback.github.githubIssueListByPage

import kotlinx.serialization.Serializable

@Serializable
data class GithubIssueByPageItem(
    val active_lock_reason: String,
    val assignee: Assignee,
    val assignees: List<Assignee>,
    val author_association: String,
    val body: String,
    val closed_at: String,
    val closed_by: ClosedBy,
    val comments: Int,
    val comments_url: String,
    val created_at: String,
    val events_url: String,
    val html_url: String,
    val id: Int,
    val labels: List<Label>,
    val labels_url: String,
    val locked: Boolean,
    val milestone: Milestone,
    val node_id: String,
    val number: Int,
    val pull_request: PullRequest,
    val repository_url: String,
    val state: String,
    val state_reason: String,
    val title: String,
    val updated_at: String,
    val url: String,
    val user: User
)