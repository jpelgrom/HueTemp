package nl.jpelgrom.huetemp.data

import androidx.room.*

@Dao
interface HueTemperatureReadingsDao {
    @Insert
    suspend fun insertReading(reading: DbTemperatureReading)

    @Query("SELECT * FROM TemperatureReadings")
    suspend fun getReadingsSynchronous(): List<DbTemperatureReading>

    @Query("SELECT * FROM TemperatureReadings WHERE sensorid = :sensorid")
    suspend fun getReadingsForSensorSynchronous(sensorid: String): List<DbTemperatureReading>

    @Update
    suspend fun updateReading(reading: DbTemperatureReading)

    @Delete
    suspend fun deleteReading(reading: DbTemperatureReading)
}