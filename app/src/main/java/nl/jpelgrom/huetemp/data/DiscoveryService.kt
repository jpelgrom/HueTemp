package nl.jpelgrom.huetemp.data

import retrofit2.http.GET

interface DiscoveryService {
    @GET("/")
    suspend fun discoverBridges(): List<DiscoveryBridge>

    companion object {
        const val API_URL = "https://discovery.meethue.com/"
    }
}