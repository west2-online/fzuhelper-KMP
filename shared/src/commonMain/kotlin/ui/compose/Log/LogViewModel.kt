package ui.compose.Log

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.futalk.kmm.NetworkErrorLog
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import di.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow

/**
 * 日志逻辑
 *
 * @property logs Flow<List<NetworkErrorLog>>
 */
class LogViewModel : ViewModel() {
  val logs: Flow<List<NetworkErrorLog>> =
    database.networkLogQueries.getAllNetworkErrorLog().asFlow().mapToList(Dispatchers.IO)
}
