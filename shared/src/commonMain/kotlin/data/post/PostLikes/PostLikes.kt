package data.post.PostLikes

import kotlinx.serialization.Serializable
import util.network.NetworkResult
import util.network.networkError

@Serializable
data class PostLikes(
    val code: Int,
    val `data`: String?,
    val msg: String
) {
    fun toNetworkResult(): NetworkResult<String> {
        val result = PostLikesResult.values().find {
            it.value == this.code
        }
        result?:run {
            return networkError("点赞失败")
        }
        return when(result){
            PostLikesResult.PostLikeMissingPostInformation,PostLikesResult.PostLikeFail -> NetworkResult.Error(Throwable("点赞失败"))
            PostLikesResult.PostLikeAlready -> NetworkResult.Error(Throwable("已经点赞了"))
            PostLikesResult.PostLikeSuccess -> NetworkResult.Success("点赞成功")
        }
    }
}
enum class PostLikesResult(val value:Int){
    PostLikeMissingPostInformation(0),
    PostLikeFail(1),
    PostLikeAlready(2),
    PostLikeSuccess(3)
}