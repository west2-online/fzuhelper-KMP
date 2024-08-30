package repository

import data.register.LoginResponse
import data.register.RegisterResponse
import di.LoginClient
import io.ktor.client.call.body
import io.ktor.client.request.cookie
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readBytes
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.http.parameters
import io.ktor.utils.io.charsets.Charset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock

/**
 * 登录的仓库层
 *
 * @property loginClient LoginClient
 * @property client HttpClient
 * @constructor
 */
class LoginRepository(private val loginClient: LoginClient) {
  val client = loginClient.client

  /**
   * 获取注册的验证码
   *
   * @param email String
   * @return Flow<RegisterResponse>
   */
  fun getRegisterCaptcha(email: String): Flow<RegisterResponse> {
    return flow {
      val response: RegisterResponse =
        client
          .submitForm(
            url = "register/captcha",
            formParameters = parameters { append("email", email) },
          ) {
            cookie("", "")
          }
          .body()
      emit(response)
    }
  }

  /**
   * 获取登录的验证码
   *
   * @param email String
   * @return Flow<LoginResponse>
   */
  fun getLoginCaptcha(email: String): Flow<LoginResponse> {
    return flow {
      val response: LoginResponse =
        client
          .submitForm(
            url = "login/captcha",
            formParameters = parameters { append("email", email) },
          ) {
            cookie("", "")
          }
          .body()
      emit(response)
    }
  }

  /**
   * 注册
   *
   * @param email String
   * @param password String
   * @param captcha String
   * @param studentCode String
   * @param studentPassword String
   * @return Flow<RegisterResponse>
   */
  fun register(
    email: String,
    password: String,
    captcha: String,
    studentCode: String,
    studentPassword: String,
  ): Flow<RegisterResponse> {
    return flow {
      val response: RegisterResponse =
        client
          .submitForm(
            url = "register/register",
            formParameters =
              parameters {
                append("email", email)
                append("password", password)
                append("captcha", captcha)
              },
            block = {
              val key = Clock.System.now().toString()
              val coding = SimpleSubstitutionCipher(key)
              //                    header("sc",coding.encrypt(studentCode))
              //                    header("sp",coding.encrypt(studentPassword))
              header("sc", coding.encrypt(studentCode))
              header("sp", coding.encrypt(studentPassword))
              header("key", key)
            },
          )
          .body()
      emit(response)
    }
  }

  /**
   * 登录
   *
   * @param email String
   * @param password String
   * @param captcha String
   * @return Flow<LoginResponse>
   */
  fun login(email: String, password: String, captcha: String): Flow<LoginResponse> {
    return flow {
      val response: LoginResponse =
        client
          .submitForm(
            url = "login/login",
            formParameters =
              parameters {
                append("email", email)
                append("password", password)
                append("captcha", captcha)
              },
          )
          .body()
      emit(response)
    }
  }

  /**
   * 验证码用户的福州大学学生身份
   *
   * @param userName String
   * @param password String
   * @param captcha String
   * @return Flow<TokenData>
   */
  fun verifyStudentIdentity(userName: String, password: String, captcha: String): Flow<TokenData> {
    return loginStudent(pass = password, user = userName, captcha = captcha)
  }

  /**
   * 登录教务处
   *
   * @param user String
   * @param pass String
   * @param captcha String
   * @return Flow<TokenData>
   */
  private fun loginStudent(user: String, pass: String, captcha: String): Flow<TokenData> {
    return flow {
      val response =
        client.submitForm(
          url = "https://jwcjwxt1.fzu.edu.cn/logincheck.asp",
          formParameters =
            Parameters.build {
              append("muser", user)
              append("passwd", pass)
              append("VerifyCode", captcha)
            },
        ) {
          contentType(ContentType.Application.FormUrlEncoded)
        }

      if (!response.status.isSuccess()) {
        println(response.status)
        throw LoginError.NetworkError.throwable
      }
      val data = response.bodyAsText(Charset.forName("GBK"))
      println(data)
      LoginError.values().forEach {
        if (data.contains(it.throwable.message.toString())) {
          throw it.throwable
        }
      }
      val token = data.split("var token = \"")[1].split("\";")[0]
      val url = response.request.url.toString()
      emit(TokenData(token = token, url = url))
    }
  }

  /**
   * 获取验证码
   *
   * @return Flow<ByteArray>
   */
  fun getVerifyCode(): Flow<ByteArray> {
    return flow {
      val response = client.get("https://jwcjwxt1.fzu.edu.cn/plus/verifycode.asp").readBytes()
      emit(response)
    }
  }
}

/**
 * flow的数据封装
 *
 * @property token String
 * @property url String
 * @constructor
 */
data class TokenData(val token: String, val url: String)

/**
 * 验证用户学生身份的可能的错误
 *
 * @property throwable Throwable
 * @constructor
 */
enum class LoginError(val throwable: Throwable) {
  TheUserDoesNotExist(Throwable("不存在该用户")),
  CaptchaVerificationFailed(Throwable("验证码验证失败")),
  ThePasswordIsIncorrect(Throwable("密码错误")),
  ErrorNotExplained(Throwable("未知错误,但不是网络错误")),
  TheOriginalPasswordIsWeak(Throwable("原密码较弱")),
  NetworkError(Throwable("网络错误")),
}

/**
 * 加密
 *
 * @property key String
 * @constructor
 */
class SimpleSubstitutionCipher(private val key: String) {
  // 加密
  fun encrypt(plainText: String): String {
    return plainText
      .map { char ->
        if (char.isLetter()) {
          val isUpperCase = char.isUpperCase()
          val index = key.indexOf(char.toUpperCase())
          val encryptedChar = if (index != -1) key[(index + 3) % key.length] else char
          if (isUpperCase) encryptedChar else encryptedChar.toLowerCase()
        } else {
          char
        }
      }
      .joinToString("")
  }

  // 解密
  fun decrypt(cipherText: String): String {
    return cipherText
      .map { char ->
        if (char.isLetter()) {
          val isUpperCase = char.isUpperCase()
          val index = key.indexOf(char.uppercaseChar())
          val decryptedChar = if (index != -1) key[(index - 3 + key.length) % key.length] else char
          if (isUpperCase) decryptedChar else decryptedChar.lowercaseChar()
        } else {
          char
        }
      }
      .joinToString("")
  }
}
