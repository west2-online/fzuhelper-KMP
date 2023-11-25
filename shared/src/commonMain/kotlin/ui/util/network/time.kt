package ui.util.network

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun String.toEasyTime(): String {
    val instant = Instant.parse(this)
    val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    return "${ localDateTime.date } ${localDateTime.hour}:${localDateTime.minute}"
}