package androidVersion

import kotlinx.serialization.Serializable

@Serializable
data class androidVersion(
    val version: List<Version>
)