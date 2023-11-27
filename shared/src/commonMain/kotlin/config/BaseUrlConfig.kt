package config

object BaseUrlConfig {
    private const val isDebug = true
    const val BaseUrl = "http://10.0.2.2:8000"
    const val UserAvatar = "${BaseUrl}/static/userAvatar"
    const val CommentImage = "${BaseUrl}/static/comment"
}