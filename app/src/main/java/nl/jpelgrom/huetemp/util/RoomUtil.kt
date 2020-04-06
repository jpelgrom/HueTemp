package nl.jpelgrom.huetemp.util

import androidx.room.TypeConverter
import nl.jpelgrom.huetemp.data.HueBridgeType

class RoomUtil {
    @TypeConverter
    fun toHueBridgeType(value: String) = enumValueOf<HueBridgeType>(value)

    @TypeConverter
    fun fromHueBridgeType(value: HueBridgeType) = value.name
}