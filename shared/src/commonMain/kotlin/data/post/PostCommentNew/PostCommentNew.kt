package data.post.PostCommentNew

import kotlinx.serialization.Serializable
import repository.CommentSubmitStatus
import util.network.NetworkResult

@Serializable
data class PostCommentNew(
    val code: Int,
    val `data`: String?,
    val msg: String?
){
    fun toNetworkResult(): NetworkResult<String> {
        val result = CommentSubmitStatus.values().find {
            it.value == this.code
        }
        return when(result){
            null ->{
                NetworkResult.Error(Throwable("评论失败,稍后再试"))
            }
            CommentSubmitStatus.CommentFailed, CommentSubmitStatus.FileParsingFailed, CommentSubmitStatus.FailedToSaveTheCommentImage->{
                NetworkResult.Error(Throwable("评论失败,稍后再试"))
            }
            CommentSubmitStatus.TheCommentIsEmpty -> {
                NetworkResult.Error(Throwable("评论不能为空"))
            }
            CommentSubmitStatus.TheReviewWasSuccessful -> {
                NetworkResult.Success("评论成功")
            }
            else -> {
                NetworkResult.Error(Throwable("评论失败,稍后再试"))
            }
        }
    }
}