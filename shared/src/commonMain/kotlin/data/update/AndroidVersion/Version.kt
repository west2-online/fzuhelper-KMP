package androidVersion

import kotlinx.serialization.Serializable

@Serializable data class Version(val canUse: Boolean, val version: String, val isLatest: Boolean)
