package nl.jpelgrom.huetemp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface HueService {
    @GET("/description.xml") // For these two calls only, use base url = bridge IP!
    suspend fun getBridgeDescription(): String?

    @POST("/api") // For these two calls only, use base url = bridge IP!
    suspend fun createUser(@Body user: HueDeviceTypeRequest): List<HueDeviceTypeResponse>
}