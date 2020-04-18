package nl.jpelgrom.huetemp.repositories

import android.content.Context
import android.util.Log
import nl.jpelgrom.huetemp.data.*
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.*
import kotlin.collections.ArrayList

class HueRepository(appContext: Context) {

    private val db by lazy {
        AppDatabase.getInstance(appContext)
    }

    private lateinit var hueService: HueService
    private var hueServiceForIP: String? = null
    private fun createHueService(ip: String, username: String) {
        if (hueServiceForIP == null || hueServiceForIP != ip) {
            hueService =
                Retrofit.Builder().baseUrl("http://$ip/api/$username/").addConverterFactory(
                    MoshiConverterFactory.create()
                )
                    .build().create(
                        HueService::class.java
                    )
            hueServiceForIP = ip
        }

    }

    suspend fun getBridges(): List<DbBridge> = db.bridges().getBridgesSynchronous()

    suspend fun getSensorsForBridge(bridge: DbBridge): List<DbSensor> =
        db.sensors().getSensorsForBridgeSynchronous(bridge.id)

    suspend fun updateSensorsForBridge(bridge: DbBridge) {
        createHueService(bridge.ip, bridge.key)

        lateinit var apiSensors: Map<String, HueSensor>
        lateinit var dbSensors: List<DbSensor>
        try {
            apiSensors = hueService.getSensors()
            dbSensors = db.sensors().getSensorsSynchronous()
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }


        val apiTemperatureSensors = apiSensors.filter { it.value.type == "ZLLTemperature" }
        val apiTemperatureSensorsList = ArrayList<DbSensor>()
        apiTemperatureSensors.forEach {
            val apiSensorId = it.key
            val apiSensorObj = it.value
            val asDbSensor = DbSensor(
                apiSensorObj.uniqueid,
                bridge.id,
                apiSensorId,
                apiSensorObj.name,
                apiSensorObj.type
            )
            if (dbSensors.any { fromDb -> fromDb.id == apiSensorObj.uniqueid }) {
                db.sensors().updateSensor(asDbSensor)
            } else {
                db.sensors().insertSensor(asDbSensor)
            }
            apiTemperatureSensorsList.add(asDbSensor)
        }

        dbSensors.forEach {
            // TODO mark as unavailable instead?
            val foundInApi = apiTemperatureSensorsList.any { fromApi -> fromApi.id == it.id }
            if (!foundInApi) {
                db.sensors().deleteSensor(it)
            }
        }
    }

    suspend fun updateTemperatureReadingForSensor(sensor: DbSensor) {
        val bridge = db.bridges().getBridge(sensor.bridgeid)
        createHueService(bridge.ip, bridge.key)

        val apiResult = hueService.getSensor(sensor.apiid)
        val dbReading = DbTemperatureReading(
            UUID.randomUUID().toString(),
            sensor.id,
            apiResult.state.temperature,
            apiResult.state.lastupdated,
            System.currentTimeMillis().toString()
        )
        Log.i("HueRepository", "Saving new reading: $dbReading")
        db.readings().insertReading(dbReading)
    }
}