package ui.compose.Report

import data.Report.reportData.ReportResponse
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.ReportRepository
import repository.ReportStatus
import util.flow.catchWithMassage
import util.flow.collectWithMassage
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.resetWithLog

class ReportViewModel(
    private val repository: ReportRepository
):ViewModel() {
    private val _reportCommentResponse = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val reportCommentResponse = _reportCommentResponse.asStateFlow()

    private val _reportPostResponse = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(
        NetworkResult.UnSend()))
    val reportPostResponse = _reportPostResponse.asStateFlow()

    fun reportComment(commentId:String,typeId:Int,postId:String){
        viewModelScope.launchInDefault {
            repository.reportComment(commentId, typeId, postId)
                .catchWithMassage {
                    _reportCommentResponse.resetWithLog(NetworkResult.Error(Throwable("举报失败")))
                }.collectWithMassage {
                    _reportCommentResponse.resetWithLog(it.toNetworkResult())
                }
        }
    }

    fun reportPost(typeId:Int,postId:String){
        viewModelScope.launchInDefault {
            repository.reportPost(typeId, postId)
                .catchWithMassage {
                    _reportPostResponse.resetWithLog(NetworkResult.Error(Throwable("举报失败")))
                }.collectWithMassage {
                    _reportPostResponse.resetWithLog(it.toNetworkResult())
                }
        }
    }
}

fun ReportResponse.toNetworkResult(): NetworkResult<String> {
    val data = ReportStatus.values().find {
        it.value == this.code
    }
    return when(data){
        null -> {
            NetworkResult.Error(Throwable("举报失败"))
        }
        ReportStatus.InsufficientInformation -> {
            NetworkResult.Success("信息不足")
        }
        ReportStatus.TheReportWasSuccessful -> {
            NetworkResult.Success("举报成功")
        }
        else -> {
            NetworkResult.Error(Throwable("举报失败"))
        }
    }
}