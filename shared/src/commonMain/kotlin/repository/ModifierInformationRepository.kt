package repository

import data.modifer.ModifierAvatar
import data.modifer.ModifierData
import doist.x.normalize.Form
import doist.x.normalize.normalize
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 更新用户信息的仓库层
 *
 * @property client HttpClient
 * @constructor
 */
class ModifierInformationRepository(val client: HttpClient) {
  /**
   * 更新用户的信息
   *
   * @param username String
   * @param age String
   * @param grade String
   * @param location String
   * @return Flow<ModifierData>
   */
  fun modifierUserData(
    username: String,
    age: String,
    grade: String,
    location: String,
  ): Flow<ModifierData> {
    return flow {
      val response =
        client
          .submitForm(
            url = "/user/userDataUpdate",
            formParameters =
              parameters {
                append("Username", username.normalize(Form.NFKD))
                append("Age", age)
                append("Grade", grade.normalize(Form.NFKD))
                append("Location", location.normalize(Form.NFKD))
              },
          )
          .body<ModifierData>()
      emit(response)
    }
  }

  /**
   * 更新用户的头像
   *
   * @param byteArray ByteArray
   * @return Flow<ModifierAvatar>
   */
  fun modifierAvatar(byteArray: ByteArray): Flow<ModifierAvatar> {
    return flow {
      val response: ModifierAvatar =
        client
          .post("/user/avatarUpdate") {
            setBody(
              MultiPartFormDataContent(
                formData {
                  append(
                    "content",
                    byteArray,
                    Headers.build {
                      append(HttpHeaders.ContentType, "image/png")
                      append("Content-Disposition", "filename=contentImage")
                    },
                  )
                }
              )
            )
          }
          .body()
      emit(response)
    }
  }
}
