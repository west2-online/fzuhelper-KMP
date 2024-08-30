package util.regex

import config.BaseUrlConfig

/**
 * 匹配邮箱
 *
 * @param email String
 * @return Boolean
 */
fun matchEmail(email: String): Boolean {
  val emailRegex = Regex("[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+")
  return emailRegex.matches(email)
}

/**
 * 匹配手机好
 *
 * @param phone String
 * @return Boolean
 */
fun matchPhone(phone: String): Boolean {
  val phoneRegex = Regex("/^1[3456789]\\d{9}\$/")
  return phoneRegex.matches(phone)
}

/**
 * 匹配http 的 url
 *
 * @param url String
 * @return Boolean
 */
fun matchHttpUrl(url: String): Boolean {
  val httpRegex =
    Regex(
      "/^(?:(http|https|ftp):\\/\\/)?((|[\\w-]+\\.)+[a-z0-9]+)(?:(\\/[^/?#]+)*)?(\\?[^#]+)?(#.+)?\$/i;"
    )
  return httpRegex.matches(url)
}

fun toGithubAvatar(login: String, userAvatarUrl: String, content: String): String {
  if (login == "FuTalkDev") {
    val id = (content).split(" From:FuTalk ").lastOrNull()
    return "${BaseUrlConfig.UserAvatar}/${id}"
  } else {
    return userAvatarUrl
  }
}

fun String.toGithubComment(): String {
  return this.split(" From:FuTalk ").firstOrNull() ?: ""
}
