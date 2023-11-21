package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class FileData(
    val fileName: String,
    override val order: Int
):PostContent