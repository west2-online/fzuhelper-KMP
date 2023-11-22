package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class FileData(
    val fileName: String,
    override val order: Int
):PostContent

@Serializable
class ImageData(
    val fileName: String,
    val imageData: ByteArray,
    override val order: Int
):PostContent