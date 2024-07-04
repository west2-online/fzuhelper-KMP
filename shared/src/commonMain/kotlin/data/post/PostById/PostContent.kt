package data.post.PostById

import io.ktor.util.decodeBase64String
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import ui.compose.Post.PostDisplayShare.LineChartDataForShow

@Serializable
data class FileData(
    val fileName: String,
    override val order: Int
):PostContent


@Serializable
data class ValueData(
    override val order: Int,
    val value: String
):PostContent


@Serializable
data class LineChartData(
    override val order: Int,
    val value: String
):PostContent

sealed interface PostContent{
    val order:Int
}

fun LineChartData.toLineChartDataForShowOrNull(): LineChartDataForShow? {
    return try {
        Json.decodeFromString<LineChartDataForShow>(this.value.decodeBase64String())
    }catch (e: Exception){
        null
    }
}

