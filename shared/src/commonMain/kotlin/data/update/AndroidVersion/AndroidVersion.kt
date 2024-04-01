package androidVersion

import kotlinx.serialization.Serializable

@Serializable
data class AndroidVersion(
    val version: List<Version>
)