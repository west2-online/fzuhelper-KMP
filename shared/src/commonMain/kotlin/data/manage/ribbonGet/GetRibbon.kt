package data.manage.ribbonGet

import kotlinx.serialization.Serializable
import util.network.NetworkResult

@Serializable
data class GetRibbon(
    val code: Int,
    val `data`: List<RibbonData>,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<List<RibbonData>> {
        val result = RibbonListResult.values().find {
            this.code == it.value
        }
        result ?:let {
            return NetworkResult.Error(Throwable("操作失败"))
        }
        result.let {
            return when(it){
                RibbonListResult.FailedToGetCarousel -> NetworkResult.Error(Throwable("获取失败"))
                RibbonListResult.SuccessToGetCarousel -> NetworkResult.Success(this.data)
            }
        }
    }
}

enum class RibbonListResult(val value :Int, val describe:String){
    FailedToGetCarousel(0,"获取失败"),
    SuccessToGetCarousel(1,"获取成功")
}