package nl.jpelgrom.huetemp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class HueBridgeType {
    V1, V2, IP
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

data class HueErrorResponse(val type: Int, val address: String, val description: String)

@Entity(tableName = "Bridges")
data class DbBridge(
    @PrimaryKey val id: String,
    val name: String,
    val key: String,
    val ip: String,
    val type: HueBridgeType
)