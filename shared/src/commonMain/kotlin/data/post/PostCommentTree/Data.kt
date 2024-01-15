package data.post.PostCommentTree

import data.share.Comment
import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val MainComment: Comment,
    val ParentComment: Comment
)