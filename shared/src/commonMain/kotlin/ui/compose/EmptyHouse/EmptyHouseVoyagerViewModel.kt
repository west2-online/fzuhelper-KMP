package ui.compose.EmptyHouse

import data.emptyRoom.EmptyItemData
import dev.icerock.moko.mvvm.flow.CMutableStateFlow
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import repository.EmptyHouseRepository
import util.flow.actionWithLabel
import util.flow.launchInDefault
import util.network.NetworkResult
import util.network.logicIfNotLoading
import util.network.networkErrorWithLog
import util.network.resetWithLog

/**
 * 空教室逻辑
 * @property emptyHouseRepository EmptyHouseRepository
 * @property _availableEmptyRoomData CMutableStateFlow<NetworkResult<Map<String, List<EmptyRoom>?>?>>
 * @property availableEmptyRoomData StateFlow<NetworkResult<Map<String, List<EmptyRoom>?>?>>
 * @constructor
 */
class EmptyHouseVoyagerViewModel(
    private val emptyHouseRepository: EmptyHouseRepository
) :ViewModel(){
    private val _availableEmptyRoomData = CMutableStateFlow(MutableStateFlow<NetworkResult<Map<String, List<EmptyItemData>?>?>>(NetworkResult.UnSend()))
    val availableEmptyRoomData = _availableEmptyRoomData.asStateFlow()

    /**
     * 获取空教室
     * @param campus String
     * @param date String
     * @param roomType String
     * @param start String
     * @param end String
     * @param build String
     */
    fun getAvailableEmptyRoomData(
        campus:String,
        date:String,
        roomType:String,
        start:String,
        end:String,
        build:List<String>,
    ){
        viewModelScope.launchInDefault {
            _availableEmptyRoomData.logicIfNotLoading {
                emptyHouseRepository.getEmptyRoom(
                    campus = campus,
                    date = date,
                    roomType = roomType,
                    start = start,
                    end = end,
                    build = build,
                ).actionWithLabel(
                    "availableEmptyRoomData/availableEmptyRoom",
                    catchAction = { label, error ->
                        _availableEmptyRoomData.resetWithLog(label, networkErrorWithLog(error,"获取空教室失败"))
                    },
                    collectAction = { label, data ->
                        _availableEmptyRoomData.resetWithLog(label,data.toNetworkResult())
                    }
                )
            }
        }
    }

}