package data.manage.ribbonDelete

import kotlinx.serialization.Serializable
import util.network.NetworkResult

@Serializable
data class RibbonDelete(
    val code: Int,
    val `data`: String?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<String> {
        val result = RibbonDeleteResult.values().find {
            this.code == it.value
        }
        result ?:let {
            return NetworkResult.Error(Throwable("删除失败"))
        }
        result.let {
            return when(it){
                RibbonDeleteResult.RibbonDeleteWithoutMassage -> NetworkResult.Error(Throwable("删除失败"))
                RibbonDeleteResult.RibbonDeleteFail -> NetworkResult.Error(Throwable("删除失败"))
                RibbonDeleteResult.RibbonDeleteSuccess -> NetworkResult.Success("删除成功")
            }
        }
    }
}

enum class RibbonDeleteResult(val value:Int,val describe:String){
    RibbonDeleteWithoutMassage(0,"删除失败"),
    RibbonDeleteFail(1,"删除失败"),
    RibbonDeleteSuccess(2,"删除成功")
}