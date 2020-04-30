package nl.jpelgrom.huetemp.util

import androidx.room.TypeConverter
import nl.jpelgrom.huetemp.data.HueBridgeType
import nl.jpelgrom.huetemp.data.HueSyncState
import java.time.ZonedDateTime

class RoomUtil {
    @TypeConverter
    fun toHueBridgeType(value: String) = enumValueOf<HueBridgeType>(value)

    @TypeConverter
    fun fromHueBridgeType(value: HueBridgeType) = value.name

    @TypeConverter
    fun toHueSyncState(value: String) = enumValueOf<HueSyncState>(value)

    @TypeConverter
    fun fromHueSyncState(value: HueSyncState) = value.name

    @TypeConverter
    fun toIso8601String(value: ZonedDateTime) = value.toOffsetDateTime().toString()

    @TypeConverter
    fun fromIso8601String(value: String): ZonedDateTime = ZonedDateTime.parse(value)
}