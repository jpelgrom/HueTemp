package nl.jpelgrom.huetemp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import nl.jpelgrom.huetemp.util.RoomUtil


@Database(entities = [DbBridge::class, DbSensor::class, DbTemperatureReading::class], version = 2)
@TypeConverters(RoomUtil::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bridges(): HueBridgesDao
    abstract fun sensors(): HueSensorsDao
    abstract fun readings(): HueTemperatureReadingsDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "huetemp.db"
            )
                .build()
    }
}