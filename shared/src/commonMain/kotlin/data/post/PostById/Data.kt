package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val Post: Post,
    val fileData: List<FileData>?,
    val valueData: List<ValueData>?
)

interface PostContent{
    val order:Int
}
data class ImageData(
    override val order: Int,
    val imageData: ByteArray,
    val fileName: String
):PostContent