package nl.jpelgrom.huetemp.data

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface HueBridgesDao {
    @Insert
    suspend fun insertBridge(bridge: DbBridge)
}