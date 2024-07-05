package ui.compose.Webview

import com.multiplatform.webview.cookie.WebViewCookieManager
import config.JWCH_BASE_URL
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import di.ClassSchedule
import io.ktor.client.plugins.cookies.cookies
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import util.CookieUtil
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.resetWithLog

class WebviewViewModel(
    val classSchedule: ClassSchedule
) : ViewModel() {
    private val _clientState = CMutableStateFlow(
        MutableStateFlow<NetworkResult<String?>>(
            NetworkResult.UnSend()
        )
    )
    val clientState = _clientState.asStateFlow()

    fun getClassScheduleClient() {
        viewModelScope.launch {
            _clientState.logicIfNotLoading {
                val client = classSchedule.getClassScheduleClient()
                if (client.first == null) {
                    _clientState.resetWithLog(
                        "getClassScheduleClient",
                        NetworkResult.Error(Throwable("获取教务处信息失败"), Throwable())
                    )
                } else {
                    val cookie = client.first!!.cookies(JWCH_BASE_URL).single {
                        it.name == "ASP.NET_SessionId"
                    }
                    val id = client.second!!
                    println("Cookie:" + cookie.toString())
                    WebViewCookieManager().setCookie(JWCH_BASE_URL, CookieUtil.transform(cookie))
                    _clientState.resetWithLog("getClassScheduleClient", NetworkResult.Success(id))
                }
            }
        }
    }
}