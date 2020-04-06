package nl.jpelgrom.huetemp.repositories

import android.content.Context
import kotlinx.coroutines.delay
import nl.jpelgrom.huetemp.data.*
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class DiscoveryRepository {
    private val discoveryService by lazy {
        Retrofit.Builder()
            .baseUrl(DiscoveryService.API_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .build().create(DiscoveryService::class.java)
    }
    private lateinit var hueService: HueService
    private lateinit var db: AppDatabase

    enum class BridgeLoginTry {
        SUCCESS,
        REJECTED,
        FAILED
    }

    private fun createHueXmlService(ip: String) =
        createHueService(ip, ScalarsConverterFactory.create())

    private fun createHueSetupService(ip: String) =
        createHueService(ip, MoshiConverterFactory.create())

    private fun createHueService(ip: String, factory: Converter.Factory) {
        hueService =
            Retrofit.Builder().baseUrl("http://$ip/").addConverterFactory(factory)
                .build().create(
                    HueService::class.java
                )
    }

    suspend fun searchForBridges(): List<DiscoveredBridge> {
        val foundBridges = arrayListOf<DiscoveredBridge>()

        try {
            val networkBridges = discoveryService.discoverBridges()
            networkBridges.forEach {
                createHueXmlService(it.internalipaddress)
                val description = hueService.getBridgeDescription()
                if (description != null) {
                    val modelName =
                        description.substringAfter("<modelName>")
                            .substringBefore("</modelName>");
                    if (modelName.contains("Philips hue bridge", true)) {
                        val modelNo = description.substringAfter("<modelNumber>")
                            .substringBefore("</modelNumber>")
                        foundBridges.add(
                            DiscoveredBridge(
                                type = if (modelNo == "BSB002") HueBridgeType.V2 else HueBridgeType.V1,
                                name = description.substringAfter("<friendlyName>")
                                    .substringBefore("</friendlyName>"),
                                ip = it.internalipaddress,
                                id = it.id
                            )
                        )
                    }
                }
            }
        } catch (e: Exception) {
            // Catch, because if it fails we can still want the manual IP option
            e.printStackTrace()
        }

        foundBridges.add(DiscoveredBridge(type = HueBridgeType.IP))
        return foundBridges
    }

    fun prepareLoginToBridge(bridge: DiscoveredBridge) {
        createHueSetupService(bridge.ip!!)
    }

    suspend fun loginToBridge(bridge: DiscoveredBridge, context: Context?): BridgeLoginTry {
        return try {
            if (context == null) {
                return BridgeLoginTry.FAILED
            }
            val result =
                hueService.createUser(HueDeviceTypeRequest(devicetype = "HueTemp#${android.os.Build.MANUFACTURER} ${android.os.Build.MODEL}"))
            when {
                result.isEmpty() -> throw Exception("Unusable response")
                result[0].success != null -> {
                    val dbBridge = DbBridge(
                        bridge.id!!,
                        bridge.name!!,
                        result[0].success!!.username,
                        bridge.ip!!,
                        bridge.type
                    )
                    db = AppDatabase.getInstance(context)
                    db.bridges().insertBridge(dbBridge)

                    BridgeLoginTry.SUCCESS
                }
                else -> {
                    delay(2_000)
                    BridgeLoginTry.REJECTED
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            BridgeLoginTry.FAILED
        }
    }
}