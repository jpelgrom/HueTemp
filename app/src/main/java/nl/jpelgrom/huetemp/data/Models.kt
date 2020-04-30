package nl.jpelgrom.huetemp.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.ZonedDateTime

enum class HueBridgeType {
    V1, V2, IP
}

enum class HueSyncState {
    AVAILABLE, UNAVAILABLE
}

data class DiscoveryBridge(val id: String, val internalipaddress: String)
data class DiscoveredBridge(
    val type: HueBridgeType,
    val ip: String? = null,
    val name: String? = null,
    val id: String? = null
)

data class HueDeviceTypeRequest(val devicetype: String)
data class HueDeviceTypeResponse(
    val success: HueDeviceTypeSuccessResponse?,
    val error: HueErrorResponse
)

data class HueDeviceTypeSuccessResponse(val username: String)

data class HueSensor(val name: String, val uniqueid: String, val type: String)
data class HueTemperatureSensor(
    val name: String,
    val uniqueid: String,
    val type: String,
    val state: HueTemperatureSensorState
)

data class HueTemperatureSensorState(val temperature: Int, val lastupdated: ZonedDateTime)

data class HueErrorResponse(val type: Int, val address: String, val description: String)

@Entity(tableName = "Bridges")
data class DbBridge(
    @PrimaryKey val id: String,
    val name: String,
    val key: String,
    val ip: String,
    val type: HueBridgeType
)

@Entity(tableName = "Sensors")
data class DbSensor(
    @PrimaryKey val id: String,
    val bridge: String,
    val apiid: String,
    val name: String,
    val type: String,
    var syncState: HueSyncState = HueSyncState.AVAILABLE
)

@Entity(tableName = "TemperatureReadings")
data class DbTemperatureReading(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val sensor: String,
    val value: Int,
    val datetime: ZonedDateTime,
    val retrieved: Long
)

data class DbBridgeWithSensors(
    @Embedded val bridge: DbBridge,
    @Relation(parentColumn = "id", entityColumn = "bridge") val sensors: List<DbSensor>
)