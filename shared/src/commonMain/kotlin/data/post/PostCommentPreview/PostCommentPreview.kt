package data.post.PostCommentPreview

import kotlinx.serialization.Serializable

@Serializable data class PostCommentPreview(val code: Int, val `data`: List<Data>, val msg: String)
