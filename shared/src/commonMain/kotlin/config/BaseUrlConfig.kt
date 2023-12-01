package config

object BaseUrlConfig {
    const val isDebug = true
    val BaseUrl = if( isDebug )"http://10.0.2.2:9077" else "http://47.116.24.13:9077"
    val UserAvatar = "${BaseUrl}/static/userAvatar"
    val CommentImage = "${BaseUrl}/static/comment"
    val PostImage = "${BaseUrl}/static/post"
    val openImage = "${BaseUrl}/openImage"
}