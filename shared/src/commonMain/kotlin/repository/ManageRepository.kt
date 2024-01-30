package repository

import data.manage.openImageList.OpenImageList
import data.manage.processPost.ProcessPost
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.request.get
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