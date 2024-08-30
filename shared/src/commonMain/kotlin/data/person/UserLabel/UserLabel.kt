package data.person.UserLabel

import data.share.Label
import kotlinx.serialization.Serializable

@Serializable data class UserLabel(val code: Int, val `data`: List<Label>, val msg: String)
