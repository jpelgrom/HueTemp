package nl.jpelgrom.huetemp.repositories

import nl.jpelgrom.huetemp.data.HueBridgeType
import nl.jpelgrom.huetemp.data.DiscoveredBridge
import nl.jpelgrom.huetemp.data.DiscoveryService
import nl.jpelgrom.huetemp.data.HueService
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
    private lateinit var hueXmlService: HueService

    private fun createHueXmlService(ip: String) {
        hueXmlService =
            Retrofit.Builder().baseUrl("http://$ip/").addConverterFactory(ScalarsConverterFactory.create())
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
                val description = hueXmlService.getBridgeDescription()
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
                                ip = it.internalipaddress
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
}