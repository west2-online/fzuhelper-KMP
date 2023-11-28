package data.post.PostComment

import data.post.PostCommentTree.MainComment
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val MainComment: MainComment,
    val SonComment: List<SonComment>
)