package repository

import data.manage.openImageAdd.OpenImageAdd
import data.manage.openImageDelete.OpenImageDelete
import data.manage.openImageList.OpenImageList
import data.manage.processPost.ProcessPost
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
import ui.util.network.NetworkResult

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
}

enum class ProcessPostStatus(val value: Int, val describe: String) {
    TheProcessingInformationIsIncorrect(0, "处理信息不正确"),
    ActivationFailed(1, "激活失败"),
    TheBanFailed(2, "封禁失败"),
    TheProcessingWasSuccessful(3, "处理成功")
}

fun ProcessPost.toNetworkResult():NetworkResult<String>{
    val result = ProcessPostStatus.values().find {
        this.code == it.value
    }
    result ?:let {
        return NetworkResult.Error(Throwable("操作失败"))
    }
    result.let {
        return when(it){
            ProcessPostStatus.TheProcessingInformationIsIncorrect -> NetworkResult.Error(Throwable("操作失败"))
            ProcessPostStatus.ActivationFailed -> NetworkResult.Error(Throwable("操作失败"))
            ProcessPostStatus.TheBanFailed -> NetworkResult.Error(Throwable("操作失败"))
            ProcessPostStatus.TheProcessingWasSuccessful -> NetworkResult.Success("处理成功")
        }
    }
}

enum class GetImageStatus(val value: Int, val describe: String) {
    FailedToGetTheImageList(0,"获取失败"),
    TheListOfImagesWasObtained(1,"获取成功")
}

fun OpenImageList.toNetworkResult():NetworkResult<List<String>>{
    val result = GetImageStatus.values().find {
        this.code == it.value
    }
    result ?:let {
        return NetworkResult.Error(Throwable("操作失败"))
    }
    result.let {
        return when(it){
            GetImageStatus.FailedToGetTheImageList -> NetworkResult.Error(Throwable("获取失败"))
            GetImageStatus.TheListOfImagesWasObtained -> NetworkResult.Success(this.data)
        }
    }
}

enum class DeletionResult(val value: Int, val description: String) {
    TheInformationToBeDeletedIsIncomplete(0, "删除信息不完整"),
    ThePictureYouWantToDeleteNotExist(1, "要删除的图片不存在"),
    RemovedUnknownErrors(2, "移除时发生未知错误"),
    DeletionFailed(3, "删除失败"),
    TheDeletionIsSuccessful(4, "删除成功")
}

fun OpenImageDelete.toNetworkResult():NetworkResult<String>{
    val result = DeletionResult.values().find {
        this.code == it.value
    }
    result ?:let {
        return NetworkResult.Error(Throwable("操作失败"))
    }
    result.let {
        return when(it){
            DeletionResult.TheInformationToBeDeletedIsIncomplete -> NetworkResult.Error(Throwable(it.description))
            DeletionResult.ThePictureYouWantToDeleteNotExist -> NetworkResult.Error(Throwable(it.description))
            DeletionResult.RemovedUnknownErrors -> NetworkResult.Error(Throwable(it.description))
            DeletionResult.DeletionFailed -> NetworkResult.Error(Throwable(it.description))
            DeletionResult.TheDeletionIsSuccessful -> NetworkResult.Success("删除成功")
        }
    }
}
enum class OpenImageAddResult(val value: Int, val description: String) {
    SplashPageFormParseFileFailed(0, "闪屏页面表单解析文件失败"),
    SplashPageFileParsingFailed(1, "闪屏页面文件解析失败"),
    SlashPageSaveFailed(2, "开屏页面无法保存评论图片"),
    SlashPageSaveSuccess(3, "斜杠页面保存失败");
}

fun OpenImageAdd.toNetworkResult():NetworkResult<String>{
    val result = OpenImageAddResult.values().find {
        this.code == it.value
    }
    result ?:let {
        return NetworkResult.Error(Throwable("操作失败"))
    }
    result.let {
        return when(it){
            OpenImageAddResult.SplashPageFormParseFileFailed -> NetworkResult.Error(Throwable("操作失败"))
            OpenImageAddResult.SplashPageFileParsingFailed -> NetworkResult.Error(Throwable("操作失败"))
            OpenImageAddResult.SlashPageSaveFailed -> NetworkResult.Error(Throwable("操作失败"))
            OpenImageAddResult.SlashPageSaveSuccess -> NetworkResult.Success("操作成功")
        }
    }
}

