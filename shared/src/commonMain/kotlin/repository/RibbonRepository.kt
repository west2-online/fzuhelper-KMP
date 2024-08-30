package repository

import data.ribbon.RibbonList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 轮播页的仓库层
 *
 * @property client HttpClient
 * @constructor
 */
class RibbonRepository(private val client: HttpClient) {
  /**
   * 获取已有轮播图列表
   *
   * @return Flow<RibbonList>
   */
  fun getRibbonList(): Flow<RibbonList> {
    return flow {
      val response = client.get("/ribbon/list").body<RibbonList>()
      emit(response)
    }
  }
}
