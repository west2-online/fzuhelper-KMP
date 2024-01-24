package data.manage.commentReportData

import data.share.Comment
import kotlinx.serialization.Serializable

@Serializable
data class CommentReportContextData(
    val CopyrightIssue: Int,
    val InappropriateContent: Int,
    val MaliciousBehavior: Int,
    val PoliticallySensitive: Int,
    val Comment: Comment,
    val PrivacyIssue: Int,
    val SpamAndAbuse: Int,
    val UnauthorizedAdvertisement: Int,
    val ViolateCommunityGuidelines: Int
)