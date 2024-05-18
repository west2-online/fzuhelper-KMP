package repository

import data.emptyRoom.EmptyRoomData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 空教室的相关仓库层 -- 需要重写
 * @property client HttpClient
 * @constructor
 */
class EmptyHouseRepository(val client:HttpClient) {
    /**
     * 用验证码获取课程
     * @param verify String
     * @param code String
     * @param campus String
     * @param build String
     * @param roomType String
     * @param date String
     * @param start String
     * @param end String
     * @param key String
     * @return Flow<EmptyRoomData>
     */
    fun refreshEmptyRoom(
        verify : String,
        code : String,
        campus : String,
        build : String,
        roomType : String,
        date : String,
        start : String,
        end : String,
        key : String,
    ):Flow<EmptyRoomData>{
        return flow {
            val response = client.submitForm(
                url = "/emptyRoom/refresh",
                formParameters = parameters {
                    append("Verify",verify)
                    append("Code",code)
                    append("Campus",campus)
                    append("Build",build)
                    append("RoomType",roomType)
                    append("Date",date)
                    append("Start",start)
                    append("End",end)
                    append("Key",key)
                }
            ).body<EmptyRoomData>()
           emit(response)
        }
    }

    /**
     * 刷新课程
     * @param campus String
     * @param date String
     * @param roomType String
     * @param start String
     * @param end String
     * @param build String
     * @return Flow<EmptyRoomData>
     */
    fun availableEmptyRoom(
        campus:String,
        date:String,
        roomType:String,
        start:String,
        end:String,
        build:String,
    ):Flow<EmptyRoomData>{
        return flow {
            val response = client.apply {

            }.submitForm(
                url = "/emptyRoom/available",
                formParameters = parameters {
                    append("Campus",campus)
                    append("Build",build)
                    append("RoomType",roomType)
                    append("Date",date)
                    append("Start",start)
                    append("End",end)
                }
            ).body<EmptyRoomData>()
            emit(response)
        }
    }
}
