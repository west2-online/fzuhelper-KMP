package data.Person.UserData

import com.bumble.appyx.utils.multiplatform.Parcelable
import com.bumble.appyx.utils.multiplatform.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class Data(
    val Id:Int,
    val Identify :Int,
    val age: Int,
    val email: String,
    val gender: String,
    val location: String,
    val username: String,
    val avatar : String
):Parcelable