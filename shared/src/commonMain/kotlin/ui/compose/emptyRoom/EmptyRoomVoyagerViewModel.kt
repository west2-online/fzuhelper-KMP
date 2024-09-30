package ui.compose.emptyRoom

import data.emptyRoom.EmptyRoom
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.EmptyRoomRepository
import util.flow.actionWithLabel2
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 空教室逻辑
 *
 * @property emptyRoomRepository EmptyRoomRepository
 * @property _availableEmptyRoomData CMutableStateFlow<NetworkResult<Map<String,
 *   List<EmptyRoom>?>?>>
 * @property availableEmptyRoomData StateFlow<NetworkResult<Map<String, List<EmptyRoom>?>?>>
 * @constructor
 */
class EmptyRoomVoyagerViewModel(private val emptyRoomRepository: EmptyRoomRepository) :
  ViewModel() {
  private val _availableEmptyRoomData =
    CMutableStateFlow(MutableStateFlow<NetworkResult<List<EmptyRoom>>>(NetworkResult.UnSend()))
  val availableEmptyRoomData = _availableEmptyRoomData.asStateFlow()

  /**
   * 获取空教室
   *
   * @param campus String
   * @param date String
   * @param start String
   * @param end String
   */
  fun getAvailableEmptyRoomData(
    campus: String,
    date: String,
    start: String,
    end: String,
  ) {
    viewModelScope.launchInDefault {
      _availableEmptyRoomData.logicIfNotLoading {
        emptyRoomRepository
          .getEmptyRoomList(
            campus = campus,
            date = date,
            startTime = start,
            endTime = end,
          )
          .actionWithLabel2(
            "availableEmptyRoomData/availableEmptyRoom",
            catchAction = { label, error ->
              _availableEmptyRoomData.resetWithLog(
                label,
                networkErrorWithLog(error, "获取空教室失败"),
              )
            },
            collectAction = { label, data ->
              _availableEmptyRoomData.resetWithLog(label, data.toNetworkResult())
            },
          )
      }
    }
  }
}
