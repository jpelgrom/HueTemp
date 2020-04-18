package nl.jpelgrom.huetemp.data

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface HueService {
    @GET("/description.xml") // For these two calls only, use base url = bridge IP!
    suspend fun getBridgeDescription(): String?

    @POST("/api") // For these two calls only, use base url = bridge IP!
    suspend fun createUser(@Body user: HueDeviceTypeRequest): List<HueDeviceTypeResponse>

    @GET("sensors")
    suspend fun getSensors(): Map<String, HueSensor>

    @GET("sensors/{id}")
    suspend fun getSensor(@Path("id") id: String): HueTemperatureSensor
}