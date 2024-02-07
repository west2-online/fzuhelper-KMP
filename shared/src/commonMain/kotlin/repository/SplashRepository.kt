package repository

import data.splash.Splash
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SplashRepository(private val client:HttpClient) {
    fun getOpenImage(): Flow<Splash> {
         return flow {
             val response :Splash = client.get("/Images/Openpage").body()
             emit(response)
         }
    }

}