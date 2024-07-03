package util

object CookieUtil {
    fun transform(cookie: io.ktor.http.Cookie): com.multiplatform.webview.cookie.Cookie {
        return com.multiplatform.webview.cookie.Cookie(
            cookie.name,
            cookie.value,
            cookie.domain,
            cookie.path,
            cookie.expires?.timestamp,
            false,
            null,
            false,//cookie.secure,   //[ERROR:cookie_manager.cc(135)] Strict Secure Cookie policy does not allow setting a secure cookie for http://jwcjwxt2.fzu.edu.cn/ for apps targeting >= R. Please either use the 'https:' scheme for this URL or omit the 'Secure' directive in the cookie value.
            cookie.httpOnly,
            cookie.maxAge.toLong(),
        )
    }
}