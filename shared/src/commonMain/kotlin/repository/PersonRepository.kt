package repository

import data.Person.UserData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PersonRepository(private val client: HttpClient) {
    fun getUserData(): Flow<UserData> {
        return flow {
            val userdata : UserData = client.get("/user/auth").body()
            emit(userdata)
        }
    }
}