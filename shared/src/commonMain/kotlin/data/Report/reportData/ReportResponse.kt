package data.Report.reportData

import kotlinx.serialization.Serializable

@Serializable
data class ReportResponse(
    val code: Int,
    val `data`: String?,
    val msg: String?
)

