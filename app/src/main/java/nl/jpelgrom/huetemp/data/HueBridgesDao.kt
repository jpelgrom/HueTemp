package nl.jpelgrom.huetemp.data

import androidx.room.*

@Dao
interface HueBridgesDao {
    @Insert
    suspend fun insertBridge(bridge: DbBridge)

    @Query("SELECT * FROM Bridges")
    suspend fun getBridgesDirect(): List<DbBridge>

    @Query("SELECT * FROM Bridges WHERE id = :id")
    suspend fun getBridge(id: String): DbBridge

    @Transaction
    @Query("SELECT * FROM Bridges WHERE id = :id")
    suspend fun getBridgeWithSensors(id: String): DbBridgeWithSensors

    @Update
    suspend fun updateBridge(bridge: DbBridge)

    @Delete
    suspend fun deleteBridge(bridge: DbBridge)
}