package data.post.PostList

import kotlinx.serialization.Serializable

@Serializable
data class Data(
    val FirstImage: String?,
    val Id: Int,
    val LikeNum: Int,
    val LittleDescribe: String?,
    val Statue: Int,
    val Time: String,
    val Title: String,
    val User: User,
    val UserId: Int
)

@Serializable
data class DataWithByteArray(
    val FirstImage: ByteArray?,
    val Id: Int,
    val LikeNum: Int,
    val LittleDescribe: String?,
    val Statue: Int,
    val Time: String,
    val Title: String,
    val User: User,
    val UserId: Int
)

suspend fun Data.toDataWithByteArray(): DataWithByteArray{
    return DataWithByteArray(
        FirstImage = null,
        Id = 0,
        LikeNum = 0,
        LittleDescribe = null,
        Statue = 0,
        Time = "",
        Title = "",
        User = this.User,
        UserId = this.UserId
    )
}