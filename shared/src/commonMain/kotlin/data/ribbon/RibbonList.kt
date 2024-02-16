package data.ribbon

import kotlinx.serialization.Serializable
import util.network.NetworkResult

@Serializable
data class RibbonList(
    val code: Int,
    val `data`: List<RibbonData>,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<List<RibbonData>> {
        val data = RibbonListResult.values().find {
            it.value == this.code
        }
        return when(data){
            RibbonListResult.FailedToGetCarousel -> NetworkResult.Error(Throwable("获取失败"))
            RibbonListResult.SuccessToGetCarousel -> NetworkResult.Success(this.data)
            null -> NetworkResult.Error(Throwable("获取失败"))
        }
    }
}

enum class RibbonListResult(val value: Int,val describe:String){
    FailedToGetCarousel(0,"无法获取轮播"),
    SuccessToGetCarousel(1,"成功获得轮播")
}
