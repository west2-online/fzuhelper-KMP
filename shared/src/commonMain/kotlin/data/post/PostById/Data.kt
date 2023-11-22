package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val Post: Post,
    val fileData: List<FileData>,
    val valueData: List<ValueData>
)



