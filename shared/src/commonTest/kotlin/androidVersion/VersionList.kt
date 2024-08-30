package androidVersion

import kotlinx.serialization.Serializable

@Serializable data class VersionList(val version: List<Version>)
