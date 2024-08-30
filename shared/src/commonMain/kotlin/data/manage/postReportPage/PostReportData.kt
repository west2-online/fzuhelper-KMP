package data.manage.postReportPage

import data.share.Post
import kotlinx.serialization.Serializable

@Serializable
data class PostReportContextData(
  val CopyrightIssue: Int,
  val InappropriateContent: Int,
  val MaliciousBehavior: Int,
  val PoliticallySensitive: Int,
  val Post: Post,
  val PrivacyIssue: Int,
  val SpamAndAbuse: Int,
  val UnauthorizedAdvertisement: Int,
  val ViolateCommunityGuidelines: Int,
)
