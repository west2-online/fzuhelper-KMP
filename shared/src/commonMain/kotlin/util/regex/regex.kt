package util.regex

fun matchEmail(email:String):Boolean{
    val emailRegex = Regex("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+")
    return emailRegex.matches(email)
}

fun matchPhone(phone:String):Boolean{
    val phoneRegex = Regex("/^1[3456789]\\d{9}\$/")
    return phoneRegex.matches(phone)
}

fun matchHttpUrl(url:String):Boolean{
    val httpRegex = Regex("/^(?:(http|https|ftp):\\/\\/)?((|[\\w-]+\\.)+[a-z0-9]+)(?:(\\/[^/?#]+)*)?(\\?[^#]+)?(#.+)?\$/i;")
    return httpRegex.matches(url)
}