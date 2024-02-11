package repository

import data.ribbon.RibbonList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RibbonRepository(
    private val client: HttpClient
) {
    fun getRibbonList():Flow<RibbonList>{
        return flow {
            val response = client.get("/ribbon/list").body<RibbonList>()
            emit(response)
        }
    }
}