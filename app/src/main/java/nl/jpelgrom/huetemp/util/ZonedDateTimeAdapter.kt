package nl.jpelgrom.huetemp.util

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

class ZonedDateTimeAdapter {
    // Hue datetime is documented as ISO-8601:2004, but missing any zone information
    // Returned strings are always in UTC, so do String <> LocalDateTime <> ZonedDateTime

    @ToJson
    fun toJson(datetime: ZonedDateTime): String = datetime.toLocalDateTime().toString()

    @FromJson
    fun fromJson(datetime: String): ZonedDateTime =
        LocalDateTime.parse(datetime).atZone(ZoneOffset.UTC)
}