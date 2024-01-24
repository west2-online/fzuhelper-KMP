package data.manage.commentReportData

import kotlinx.serialization.Serializable

@Serializable
data class CommentReportForResponseList(
    val code: Int,
    val `data`: List<CommentReportContextData>,
    val msg: String
)