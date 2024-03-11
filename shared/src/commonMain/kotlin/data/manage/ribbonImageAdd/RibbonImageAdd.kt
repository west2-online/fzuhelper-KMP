package data.manage.ribbonImageAdd

import kotlinx.serialization.Serializable
import util.network.NetworkResult

@Serializable
data class RibbonImageAdd(
    val code: Int,
    val `data`: String?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<String> {
        val result = RibbonAddResult.values().find {
            this.code == it.value
        }
        result ?:let {
            return NetworkResult.Error(Throwable("操作失败"))
        }
        result.let {
            return when(it){
                RibbonAddResult.RibbonFileAddSuccess -> NetworkResult.Success("添加成功")
                else -> NetworkResult.Error(Throwable("添加失败"))
            }
        }
    }
}

enum class RibbonAddResult(
    val value:Int,
    val describe:String
){
    RibbonFileOpenFail(0, describe = "RibbonFileOpenFile"),
    RibbonFileCreateFail(1, describe = "RibbonFileCreateFail"),
    RibbonFileSaveFail(2, describe = "RibbonFileSaveFail"),
    RibbonFileSaveToMysqlFail(3, describe = "RibbonFileSaveToMysqlFail"),
    RibbonFileAddSuccess(4, describe = "RibbonFileAddSuccess"),
    RibbonFileAddParseFail(5, describe = "RibbonFileAddParseFail"),
}