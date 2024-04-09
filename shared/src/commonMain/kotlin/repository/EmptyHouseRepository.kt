package repository

import data.emptyRoom.EmptyRoomData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EmptyHouseRepository(val client:HttpClient) {

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
