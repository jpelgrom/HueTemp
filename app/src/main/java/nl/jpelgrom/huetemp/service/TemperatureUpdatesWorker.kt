package nl.jpelgrom.huetemp.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import nl.jpelgrom.huetemp.repositories.HueRepository
import java.io.IOException

class TemperatureUpdatesWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        val repo = HueRepository(applicationContext)

        // TODO check network
        try {
            val bridges = repo.getBridges()
            bridges.forEach { bridge ->
                repo.updateSensorsForBridge(bridge)

                val sensors = repo.getSensorsForBridge(bridge)
                sensors.forEach { sensor ->
                    repo.updateTemperatureReadingForSensor(sensor)
                }
            }

            Result.success()
        } catch (e: IOException) {
            // Catch because we don't want the job to fail and get exponentially delayed
            e.printStackTrace()
            Result.success()
        }
    }
}