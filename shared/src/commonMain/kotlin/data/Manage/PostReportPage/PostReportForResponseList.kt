package data.Manage.PostReportPage

import kotlinx.serialization.Serializable

@Serializable
data class PostReportForResponseList(
    val code: Int,
    val `data`: List<PostReportContextData>,
    val msg: String
)