package repository

import data.manage.addAdmin.AdminAdd
import data.manage.adminLevelUpdate.AdminLevelUpdate
import data.manage.adminList.AdminList
import data.manage.openImageAdd.OpenImageAdd
import data.manage.openImageDelete.OpenImageDelete
import data.manage.openImageList.OpenImageList
import data.manage.processPost.ProcessPost
import data.manage.ribbonDelete.RibbonDelete
import data.manage.ribbonGet.GetRibbon
import data.manage.ribbonImageAdd.RibbonImageAdd
import data.manage.userDataByEmail.UserDataByEmail
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.request.get
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ManageRepository(
    val client: HttpClient
) {
    fun processPost(postId:Int, status: Int): Flow<ProcessPost> {
        return flow<ProcessPost> {
            val result = client.submitForm(
                "/manage/post/process",
                formParameters = parameters{
                    append("postId",postId.toString())
                    append("status",status.toString())
                }
            ).body<ProcessPost>()
            emit(result)
        }
    }

    fun getImageList(): Flow<OpenImageList> {
        return flow<OpenImageList> {
            val result = client.get("/manage/openImage/list").body<OpenImageList>()
            emit(result)
        }
    }

    fun getRibbonList(): Flow<GetRibbon> {
        return flow<GetRibbon> {
            val result = client.get("/manage/ribbon/list").body<GetRibbon>()
            emit(result)
        }
    }

    fun processComment(commentId:Int,postId:Int, status: Int): Flow<ProcessPost> {
        return flow<ProcessPost> {
            val result = client.submitForm(
                "/manage/comment/process",
                formParameters = parameters{
                    append("commentId",commentId.toString())
                    append("status",status.toString())
                    append("postId",postId.toString())
                }
            ).body<ProcessPost>()
            emit(result)
        }
    }

    fun deleteOpenImage(imageName:String):Flow<OpenImageDelete>{
        return flow {
            val result = client.submitForm(
                "/manage/openImage/delete",
                formParameters = parameters{
                    append("imageName",imageName)
                }
            ).body<OpenImageDelete>()
            emit(result)
        }
    }

    fun addNewOpenImage(openImage: ByteArray): Flow<OpenImageAdd> {
        return flow {
            val response = client.submitFormWithBinaryData(
                url = "/manage/openImage/add",
                formData = formData {
                    append("slashPage", openImage, Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=\"ktor_logo.png\"")
                    })
                }
            ).body<OpenImageAdd>()
            emit(response)
        }

    }

    fun deleteRibbon(imageName : String):Flow<RibbonDelete>{
        return flow {
            val result = client.submitForm(
                "/manage/ribbon/delete",
                formParameters = parameters{
                    append("ribbon",imageName)
                }
            ).body<RibbonDelete>()
            emit(result)
        }
    }

    fun addNewRibbonImage(ribbonImage: ByteArray,ribbonAction:String): Flow<RibbonImageAdd> {
        return flow {
            val response = client.submitFormWithBinaryData(
                url = "/manage/ribbon/add",
                formData = formData {
                    append("ribbon", ribbonImage, Headers.build {
                        append(HttpHeaders.ContentType, "image/png")
                        append(HttpHeaders.ContentDisposition, "filename=\"ktor_logo.png\"")
                    })
                    append("ribbonAction", ribbonAction)
                }
            ).body<RibbonImageAdd>()
            emit(response)
        }
    }

    fun getEmailByEmail(email:String):Flow<UserDataByEmail>{
        return flow {
            val response = client.get("/manage/user"){
                url {
                    parameters.append("email", email)
                }
            }.body<UserDataByEmail>()
            emit(response)
        }
    }

    fun getAdminList():Flow<AdminList>{
        return flow {
            val response = client.get("/manage/admins").body<AdminList>()
            emit(response)
        }
    }

    fun addAdmin(email: String):Flow<AdminAdd>{
        return flow {
            val response = client.submitForm (url = "/manage/admin"){
                formData {
                    append("email",email)
                }
            }.body<AdminAdd>()
            emit(response)
        }
    }

    fun updateAdminLevel(level:Int,userId :Int): Flow<AdminLevelUpdate> {
        return flow<AdminLevelUpdate> {
            val response = client.submitForm("/manage/adminUpdate") {
                formData {
                    append("userId",userId)
                    append("level",level)
                }
            }.body<AdminLevelUpdate>()
            emit(response)
        }
    }
}




enum class GetImageStatus(val value: Int, val describe: String) {
    FailedToGetTheImageList(0,"获取失败"),
    TheListOfImagesWasObtained(1,"获取成功")
}







