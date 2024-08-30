package repository

import data.person.UserData.UserData
import data.person.identity.PersonIdentityData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 个人信息的仓库层
 *
 * @property client HttpClient
 * @constructor
 */
class PersonRepository(private val client: HttpClient) {
  /**
   * 获取自己的信息
   *
   * @return Flow<UserData>
   */
  fun getUserDataMySelf(): Flow<UserData> {
    return flow {
      val userdata: UserData = client.get("/user/auth").body()
      emit(userdata)
    }
  }

  /**
   * 获取自己的身份
   *
   * @return Flow<PersonIdentityData>
   */
  fun getUserIdentityMySelf(): Flow<PersonIdentityData> {
    return flow {
      val userdata: PersonIdentityData = client.get("/user/identity").body()
      emit(userdata)
    }
  }

  /**
   * 获取他人的个人信息
   *
   * @param id String
   * @return Flow<UserData>
   */
  fun getUserDataOther(id: String): Flow<UserData> {
    return flow {
      val userdata: UserData = client.get("/user/auth/${id}").body()
      emit(userdata)
    }
  }

  /**
   * 获取他人的身份信息
   *
   * @param id String
   * @return Flow<PersonIdentityData>
   */
  fun getUserIdentityOther(id: String): Flow<PersonIdentityData> {
    return flow {
      val userdata: PersonIdentityData = client.get("/user/identity/${id}").body()
      emit(userdata)
    }
  }
}
