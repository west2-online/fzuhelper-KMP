package repository

import data.base.BaseResponseData
import data.emptyRoom.EmptyRoom
import di.FzuHelperClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 空教室的相关仓库层
 *
 * @property client FzuHelperClient
 * @constructor
 */
class EmptyRoomRepository(val client: FzuHelperClient) {
  /**
   * 刷新课程
   *
   * @param campus String
   * @param date String
   * @param startTime String
   * @param endTime String
   * @return Flow<List<EmptyRoom>>
   */
  fun getEmptyRoomList(
    campus: String,
    date: String,
    startTime: String,
    endTime: String
  ): Flow<BaseResponseData<List<EmptyRoom>>> {
    return flow {
      val response =
        client.client.get("/api/v1/common/classroom/empty") {
          parameter("date", date)
          parameter("campus", campus)
          parameter("startTime", startTime)
          parameter("endTime", endTime)
        }.body<BaseResponseData<List<EmptyRoom>>>()
      emit(response)
    }
  }
}
