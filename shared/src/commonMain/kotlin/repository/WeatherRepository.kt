package repository

import ShareClient
import data.weather.WeatherData
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class WeatherRepository(val client: ShareClient) {
    fun getWeatherOfFuZhou(): Flow<WeatherData> {
        return flow {
            val response = client.client.get("http://t.weather.itboy.net/api/weather/city/101230103").body<WeatherData>()
            emit(response)
        }
    }
}