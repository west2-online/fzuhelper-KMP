package data.manage.adminList

import data.share.User
import kotlinx.serialization.Serializable

@Serializable data class Admin(val Id: Int, val Level: Int, val User: User, val UserId: Int)
