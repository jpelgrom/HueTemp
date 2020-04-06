package nl.jpelgrom.huetemp.data

import retrofit2.http.GET

interface HueService {
    @GET("/description.xml") // For this call only, use base url = bridge IP!
    suspend fun getBridgeDescription(): String?
}