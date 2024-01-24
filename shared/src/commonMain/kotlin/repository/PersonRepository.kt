package repository

import data.person.UserData.UserData
import data.person.identity.PersonIdentityData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PersonRepository(private val client: HttpClient) {
    fun getUserDataMySelf(): Flow<UserData> {
        return flow {
            val userdata : UserData = client.get("/user/auth").body()
            emit(userdata)
        }
    }
    fun getUserIdentityMySelf(): Flow<PersonIdentityData> {
        return flow {
            val userdata : PersonIdentityData = client.get("/user/identity").body()
            emit(userdata)
        }
    }
    fun getUserDataOther(id:String): Flow<UserData> {
        return flow {
            val userdata : UserData = client.get("/user/auth/${id}").body()
            emit(userdata)
        }
    }
    fun getUserIdentityOther(id:String): Flow<PersonIdentityData> {
        return flow {
            val userdata : PersonIdentityData = client.get("/user/identity/${id}").body()
            emit(userdata)
        }
    }
}