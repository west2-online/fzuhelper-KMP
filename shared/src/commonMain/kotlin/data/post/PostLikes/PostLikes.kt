package data.post.PostLikes

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkErrorWithLog

@Serializable
data class PostLikes(
    val code: Int,
    val `data`: String?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<String> {
        return when(code){
            0 ->  networkErrorWithLog(code,"已经点赞了")
            1 -> NetworkResult.Success("点赞成功")
            else -> networkErrorWithLog(code,"点赞失败")
        }
    }
}
