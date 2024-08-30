package data.post.PostCommentPreview

import data.share.Comment
import kotlinx.serialization.Serializable

@Serializable data class Data(val MainComment: Comment, val SonComment: List<Comment>)
