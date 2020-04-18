package nl.jpelgrom.huetemp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

        private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE TABLE IF NOT EXISTS `Sensors` (`id` TEXT NOT NULL, `bridgeid` TEXT NOT NULL, `apiid` TEXT NOT NULL, `name` TEXT NOT NULL, `type` TEXT NOT NULL, PRIMARY KEY(`id`))")
                database.execSQL("CREATE TABLE IF NOT EXISTS `TemperatureReadings` (`id` TEXT NOT NULL, `sensorid` TEXT NOT NULL, `value` INTEGER NOT NULL, `datetime` TEXT NOT NULL, `retrieved` TEXT NOT NULL, PRIMARY KEY(`id`))")
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java, "huetemp.db"
            )
                .addMigrations(MIGRATION_1_2)
                .build()
    }
}