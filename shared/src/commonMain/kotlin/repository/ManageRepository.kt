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

/**
 * 管理员仓库层
 * @property client HttpClient
 * @constructor
 */
class ManageRepository(
    val client: HttpClient
) {
    /**
     * 处理据举报的帖子
     * @param postId Int
     * @param status Int
     * @return Flow<ProcessPost>
     */
    fun processPost(postId:Int, status: Int): Flow<ProcessPost> {
        return flow<ProcessPost> {
            val result = client.submitForm(
                "/manage/post/process",
                formParameters = parameters{
                    append("PostId",postId.toString())
                    append("Status",status.toString())
                }
            ).body<ProcessPost>()
            emit(result)
        }
    }

    /**
     * 获取已有的开屏页列表
     * @return Flow<OpenImageList>
     */
    fun getImageList(): Flow<OpenImageList> {
        return flow<OpenImageList> {
            val result = client.get("/manage/openImage/list").body<OpenImageList>()
            emit(result)
        }
    }

    /**
     * 获取已有轮播图列表
     * @return Flow<GetRibbon>
     */
    fun getRibbonList(): Flow<GetRibbon> {
        return flow<GetRibbon> {
            val result = client.get("/manage/ribbon/list").body<GetRibbon>()
            emit(result)
        }
    }

    /**
     * 处理评论
     * @param commentId Int
     * @param postId Int
     * @param status Int
     * @return Flow<ProcessPost>
     */
    fun processComment(commentId:Int,postId:Int, status: Int): Flow<ProcessPost> {
        return flow<ProcessPost> {
            val result = client.submitForm(
                "/manage/comment/process",
                formParameters = parameters{
                    append("CommentId",commentId.toString())
                    append("Status",status.toString())
                    append("PostId",postId.toString())
                }
            ).body<ProcessPost>()
            emit(result)
        }
    }

    /**
     * 删除开屏页
     * @param imageName String
     * @return Flow<OpenImageDelete>
     */
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

    /**
     * 添加新的开屏页
     * @param openImage ByteArray
     * @return Flow<OpenImageAdd>
     */
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

    /**
     * 删除特定的轮播页
     * @param imageName String
     * @return Flow<RibbonDelete>
     */
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

    /**
     * 添加新的轮播图
     * @param ribbonImage ByteArray
     * @param ribbonAction String
     * @return Flow<RibbonImageAdd>
     */
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

    /**
     * 用邮件获取用户信息
     * @param email String
     * @return Flow<UserDataByEmail>
     */
    fun getUserByEmail(email:String):Flow<UserDataByEmail>{
        return flow {
            val response = client.get("/manage/user"){
                url {
                    parameters.append("email", email)
                }
            }.body<UserDataByEmail>()
            emit(response)
        }
    }

    /**
     * 获取管理员列表
     * @return Flow<AdminList>
     */
    fun getAdminList():Flow<AdminList>{
        return flow {
            val response = client.get("/manage/admins").body<AdminList>()
            emit(response)
        }
    }

    /**
     * 添加新的管理员
     * @param email String
     * @return Flow<AdminAdd>
     */
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

    /**
     * 更新管理员等级
     * @param level Int
     * @param userId Int
     * @return Flow<AdminLevelUpdate>
     */
    fun updateAdminLevel(level:Int,userId :Int): Flow<AdminLevelUpdate> {
        return flow<AdminLevelUpdate> {
            val response = client.submitForm("/manage/adminUpdate") {
                formData {
                    append("UserId",userId)
                    append("Level",level)
                }
            }.body<AdminLevelUpdate>()
            emit(response)
        }
    }
}







