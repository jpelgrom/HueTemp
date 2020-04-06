package nl.jpelgrom.huetemp.data

enum class HueBridgeType {
    V1, V2, IP
}
data class DiscoveryBridge(val id: String, val internalipaddress: String)
data class DiscoveredBridge(val type: HueBridgeType, val ip: String? = null, val name: String? = null)