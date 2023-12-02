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
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow


class ModifierInformationRepository(val client: HttpClient) {
    fun modifierUserData(username:String, age:String, grade:String, location:String): Flow<ModifierData> {
        return flow {
            val response = client.submitForm(
                url = "/user/userDataUpdate",
                formParameters = parameters {
                    append("username",username.normalize(Form.NFKD))
                    append("age",age)
                    append("grade",grade.normalize(Form.NFKD))
                    append("location",location.normalize(Form.NFKD))
                }
            ).body<ModifierData>()
            emit(response)
        }
    }
    fun modifierAvatar(byteArray: ByteArray):Flow<ModifierAvatar>{
        return flow {
            val response : ModifierAvatar = client.post("/user/avatarUpdate"){
                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append("data",byteArray,
                                Headers.build {
                                    append("isImage", "true")
                                    append("Content-Disposition", "form-data; name=\"file\"; filename=\"${1}\"")
                                }
                            )
                        }
                    )
                )
            }.body()
            emit(response)
        }
    }
}

enum class ModifierDataStatus(val value: Int, val describe: String) {
    AgeMustBeANumber(0, "年龄必须是数字"),
    TheUpdateFailed(1, "更新失败"),
    UpdateSuccessful(2, "更新成功")
}


enum class ModifierAvatarStatus(val value: Int, val describe: String) {
    FileParsingFailed(0, "解析失败"),
    FailedToSaveTheAvatar(1, "更新失败"),
    FailedToUpdateTheAvatar(2, "更新失败"),
    UpdateAvatarSuccessful(3,"更新成功")
}