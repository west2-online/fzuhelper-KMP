package data.manage.ribbonDelete

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

private const val RibbonDeleteWithoutMassage = 0
private const val RibbonDeleteFail = 1
private const val RibbonDeleteSuccess = 2
@Serializable
data class RibbonDelete(
    val code: Int,
    val `data`: String?,
    val msg: String
) {

    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            RibbonDeleteWithoutMassage,RibbonDeleteFail -> networkErrorWithLog(code,"删除失败")
            RibbonDeleteSuccess -> NetworkResult.Success("删除成功")
            else -> networkErrorWithLog(code,"删除失败")
        }
    }

}

enum class RibbonDeleteResult(val value:Int,val describe:String){
    RibbonDeleteWithoutMassage(0,"删除失败"),
    RibbonDeleteFail(1,"删除失败"),
    RibbonDeleteSuccess(2,"删除成功")
}

