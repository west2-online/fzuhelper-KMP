package data.Report.reportData

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class ReportResponse(
    val code: Int,
    val `data`: String?,
    val msg: String?
){
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            1 -> networkErrorWithLog(code,"举报失败")
            0 -> networkErrorWithLog(code,"信心不足")
            2 -> NetworkResult.Success("举报成功")
            else -> networkErrorWithLog(code,"举报失败")
        }
    }
}
