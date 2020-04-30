package nl.jpelgrom.huetemp.repositories

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import nl.jpelgrom.huetemp.data.*
import nl.jpelgrom.huetemp.util.ZonedDateTimeAdapter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HueRepository(appContext: Context) {

    private val db by lazy {
        AppDatabase.getInstance(appContext)
    }

    private lateinit var hueService: HueService
    private var hueServiceForIP: String? = null
    private fun createHueService(ip: String, username: String) {
        if (hueServiceForIP == null || hueServiceForIP != ip) {
            val moshi = Moshi.Builder().add(ZonedDateTimeAdapter()).build()
            hueService =
                Retrofit.Builder().baseUrl("http://$ip/api/$username/").addConverterFactory(
                    MoshiConverterFactory.create(moshi)
                )
                    .build().create(
                        HueService::class.java
                    )
            hueServiceForIP = ip
        }

    }

    suspend fun getBridges(): List<DbBridge> = db.bridges().getBridgesDirect()

    suspend fun getSensorsForBridge(bridge: DbBridge): List<DbSensor> =
        db.sensors().getSensorsForBridgeDirect(bridge.id)

    suspend fun updateSensorsForBridge(bridge: DbBridge) {
        createHueService(bridge.ip, bridge.key)

        val apiSensors = hueService.getSensors()
        val dbSensors = db.sensors().getSensorsDirect()

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
            val foundInApi = apiTemperatureSensorsList.any { fromApi -> fromApi.id == it.id }
            if (!foundInApi) {
                it.syncState = HueSyncState.UNAVAILABLE
                db.sensors().updateSensor(it)
            }
        }
    }

    suspend fun updateTemperatureReadingForSensor(sensor: DbSensor) {
        val bridge = db.bridges().getBridge(sensor.bridge)
        createHueService(bridge.ip, bridge.key)

        val apiResult = hueService.getSensor(sensor.apiid)
        val dbReading = DbTemperatureReading(
            sensor = sensor.id,
            value = apiResult.state.temperature,
            datetime = apiResult.state.lastupdated,
            retrieved = System.currentTimeMillis()
        )
        Log.i("HueRepository", "Saving new reading: $dbReading")
        db.readings().insertReading(dbReading)
    }
}