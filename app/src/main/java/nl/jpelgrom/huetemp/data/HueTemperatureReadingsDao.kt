package nl.jpelgrom.huetemp.data

import androidx.room.*

@Dao
interface HueTemperatureReadingsDao {
    @Insert
    suspend fun insertReading(reading: DbTemperatureReading)

    @Query("SELECT * FROM TemperatureReadings")
    suspend fun getReadingsDirect(): List<DbTemperatureReading>

    @Query("SELECT * FROM TemperatureReadings WHERE sensor = :sensor")
    suspend fun getReadingsForSensorDirect(sensor: String): List<DbTemperatureReading>

    @Update
    suspend fun updateReading(reading: DbTemperatureReading)

    @Delete
    suspend fun deleteReading(reading: DbTemperatureReading)
}