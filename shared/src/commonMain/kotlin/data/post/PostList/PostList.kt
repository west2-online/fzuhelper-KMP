package data.post.PostList

import data.share.Label
import data.share.Post
import kotlinx.serialization.Serializable

@Serializable
data class PostList(val code: Int, val `data`: List<PostListItemData>?, val msg: String)

@Serializable data class PostListItemData(val Post: Post, val Labels: List<Label>?)
