package nl.jpelgrom.huetemp.data

import androidx.room.*

@Dao
interface HueSensorsDao {
    @Insert
    suspend fun insertSensor(sensor: DbSensor)

    @Query("SELECT * FROM Sensors")
    suspend fun getSensorsDirect(): List<DbSensor>

    @Query("SELECT * FROM Sensors WHERE bridge = :bridge")
    suspend fun getSensorsForBridgeDirect(bridge: String): List<DbSensor>

    @Query("SELECT * FROM Sensors WHERE id = :id")
    suspend fun getSensor(id: String): DbSensor

    @Update
    suspend fun updateSensor(sensor: DbSensor)

    @Delete
    suspend fun deleteSensor(sensor: DbSensor)
}