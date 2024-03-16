package config

object BaseUrlConfig {
    const val isDebug = true
//    val BaseUrl = if( isDebug )"http://10.0.2.2:9077" else "http://47.116.24.13:9077"
//const val BaseUrl = "http://47.116.24.13:9077"
    const val BaseUrl = "http://10.0.2.2:9077"
    const val UserAvatar = "${BaseUrl}/static/userAvatar"
    const val CommentImage = "${BaseUrl}/static/comment"
    const val PostImage = "${BaseUrl}/static/post"
    const val RibbonImage = "${BaseUrl}/static/ribbon"
    const val OpenImage = "${BaseUrl}/openImage"
}