package data.post.PostCommentTree

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val MainComment: MainComment,
    val ParentComment: ParentComment
)