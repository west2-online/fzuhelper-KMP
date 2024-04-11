package config

object BaseUrlConfig {
    const val isDebug = true
//    val BaseUrl = "http://47.116.24.13:9077"
//    if( isDebug )"http://10.0.2.2:9077" else
//    const val BaseUrl = "http://47.116.24.13:9077"
    const val BaseUrl = "http://10.0.2.2:9077"
//    const val BaseUrl =  "http://192.168.1.233:9077"
    val UserAvatar = "${BaseUrl}/static/userAvatar"
    val CommentImage = "${BaseUrl}/static/comment"
    val PostImage = "${BaseUrl}/static/post"
    val RibbonImage = "${BaseUrl}/static/ribbon"
    val OpenImage = "${BaseUrl}/openImage"
}