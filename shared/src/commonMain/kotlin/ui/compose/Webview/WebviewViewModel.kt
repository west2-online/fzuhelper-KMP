package ui.compose.Webview

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
        MutableStateFlow<NetworkResult<IdWithCookie>>(
            NetworkResult.UnSend()
        )
    )
    val clientState = _clientState.asStateFlow()

    fun getClassScheduleClient() {
        viewModelScope.launch {
            _clientState.logicIfNotLoading {
                val client = classSchedule.getClassScheduleClient()
                if (client.first == null || client.second == null) {
                    _clientState.resetWithLog(
                        "getClassScheduleClient",
                        NetworkResult.Error(Throwable("获取教务处信息失败"), Throwable())
                    )
                } else {
                    val cookie = client.first!!.cookies(JWCH_BASE_URL)
                    val id = client.second!!
                    println("Cookie:" + cookie.toString())
//                    cookie.forEach { cookieItem ->
//                        WebViewCookieManager().setCookie(JWCH_BASE_URL, CookieUtil.transform(cookieItem))
//                    }
                    _clientState.resetWithLog("getClassScheduleClient", NetworkResult.Success(
                        IdWithCookie(
                            id,cookie.map { cookieItem ->
                                CookieUtil.transform(cookieItem)
                            }
                        )
                    ))
                }
            }
        }
    }
}

class IdWithCookie (
    val id:String,
    val cookie:List<com.multiplatform.webview.cookie.Cookie>
)