package ui.compose.EmptyHouse

import data.emptyRoom.EmptyRoom
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
    private val _availableEmptyRoomData = CMutableStateFlow(MutableStateFlow<NetworkResult<Map<String, List<EmptyRoom>?>?>>(NetworkResult.UnSend()))
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
        build:String,
    ){
        viewModelScope.launchInDefault {
            _availableEmptyRoomData.logicIfNotLoading {
                emptyHouseRepository.availableEmptyRoom(
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

    /**
     * 用验证码获取空教室
     * @param verify String
     * @param code String
     * @param campus String
     * @param build String
     * @param roomType String
     * @param date String
     * @param start String
     * @param end String
     * @param key String
     */
    fun refreshEmptyClassRoom(
        verify : String,
        code : String,
        campus : String,
        build : String,
        roomType : String,
        date : String,
        start : String,
        end : String,
        key : String,
    ){
        viewModelScope.launchInDefault {
            _availableEmptyRoomData.logicIfNotLoading {
                emptyHouseRepository.refreshEmptyRoom(
                    verify = verify,
                    code = code,
                    campus = campus,
                    build = build,
                    roomType = roomType,
                    date = date,
                    start = start,
                    end = end,
                    key = key,
                ).actionWithLabel(
                    "refreshEmptyClassRoom/refreshEmptyRoom",
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