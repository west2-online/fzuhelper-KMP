package data.post.PostComment

data class Post(
    val FirstImage: String,
    val Id: Int,
    val LikeNum: Int,
    val LittleDescribe: String,
    val Status: Int,
    val Time: String,
    val Title: String,
    val User: UserX,
    val UserId: Int
)