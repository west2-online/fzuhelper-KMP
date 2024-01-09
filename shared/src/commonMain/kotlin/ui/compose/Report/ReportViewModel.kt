package ui.compose.Report

import data.Report.reportData.ReportResponse
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.ReportRepository
import repository.ReportStatus
import ui.util.flow.catchWithMassage
import ui.util.flow.collectWithMassage
import ui.util.flow.launchInDefault
import ui.util.network.NetworkResult
import ui.util.network.reset

class ReportViewModel(
    private val repository: ReportRepository
):ViewModel() {
    private val _reportResponse = CMutableStateFlow(MutableStateFlow<NetworkResult<String>>(NetworkResult.UnSend()))
    val reportResponse = _reportResponse.asStateFlow()
    fun reportComment(commentId:String,typeId:Int,postId:String){
        viewModelScope.launchInDefault {
            repository.reportComment(commentId, typeId, postId)
                .catchWithMassage {
                    _reportResponse.reset(NetworkResult.Error(Throwable("举报失败")))
                }.collectWithMassage {
                    _reportResponse.reset(it.toNetworkResult())
                }
        }
    }

    fun reportPost(typeId:Int,postId:String){
        viewModelScope.launchInDefault {
            repository.reportPost(typeId, postId)
                .catchWithMassage {
                    _reportResponse.reset(NetworkResult.Error(Throwable("举报失败")))
                }.collectWithMassage {

                }
        }
    }
}

fun ReportResponse.toNetworkResult():NetworkResult<String>{
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