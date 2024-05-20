package repository

import data.emptyRoom.EmptyData
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
     * 刷新课程
     * @param campus String
     * @param date String
     * @param roomType String
     * @param start String
     * @param end String
     * @param build String
     * @return Flow<EmptyRoomData>
     */
    fun getEmptyRoom(
        campus:String,
        date:String,
        roomType:String,
        start:String,
        end:String,
        build:List<String>,
    ):Flow<EmptyData>{
        return flow {
            val response = client.apply {

            }.submitForm(
                url = "/emptyRoom/class",
                formParameters = parameters {
                    append("Campus",campus)
                    build.forEach {
                        append("Build",it)
                    }
                    append("RoomType",roomType)
                    append("Date",date)
                    append("Start",start)
                    append("End",end)
                }
            ).body<EmptyData>()
            emit(response)
        }
    }
}
