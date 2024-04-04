package data.post.PostById

import kotlinx.serialization.Serializable

@Serializable
data class FileData(
    val fileName: String,
    override val order: Int
):PostContent


@Serializable
data class ValueData(
    override val order: Int,
    val value: String
):PostContent

@Serializable
data class NavigationData(
    override val order: Int,
    val navigation :String
):PostContent


interface PostContent{
    val order:Int
}