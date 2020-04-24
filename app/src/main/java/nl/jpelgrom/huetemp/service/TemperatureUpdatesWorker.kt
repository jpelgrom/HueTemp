package nl.jpelgrom.huetemp.service

import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import nl.jpelgrom.huetemp.R
import nl.jpelgrom.huetemp.repositories.HueRepository
import nl.jpelgrom.huetemp.util.NotificationUtil
import java.io.IOException


class TemperatureUpdatesWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result = coroutineScope {
        val repo = HueRepository(applicationContext)
        setForeground(createForegroundInfo(SyncState.START))

        // TODO check network
        try {
            val bridges = repo.getBridges()
            bridges.forEach { bridge ->
                setForeground(createForegroundInfo(SyncState.BRIDGE))
                repo.updateSensorsForBridge(bridge)

                val sensors = repo.getSensorsForBridge(bridge)
                sensors.forEach { sensor ->
                    setForeground(createForegroundInfo(SyncState.SENSOR))
                    repo.updateTemperatureReadingForSensor(sensor)
                }
            }
        } catch (e: IOException) {
            // Catch because we don't want the job to fail and get exponentially delayed
            e.printStackTrace()
            Result.success()
        } finally {
            setForeground(createForegroundInfo(SyncState.DONE))
        }
        Result.success()
    }

    private fun createForegroundInfo(progress: SyncState): ForegroundInfo {
        val title =
            applicationContext.getString(R.string.notifications_worker_temperaturesync_title)
        val content = applicationContext.getString(
            when (progress) {
                SyncState.BRIDGE -> R.string.notifications_worker_temperaturesync_bridge
                SyncState.SENSOR -> R.string.notifications_worker_temperaturesync_sensor
                SyncState.DONE -> R.string.notifications_worker_temperaturesync_done
                else -> R.string.notifications_worker_temperaturesync_start
            }
        )
        val cancel =
            applicationContext.getString(R.string.notifications_worker_temperaturesync_cancel)
        val intent = WorkManager.getInstance(applicationContext)
            .createCancelPendingIntent(id)

        if (progress == SyncState.START && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationUtil().createChannelTemperatureSync(applicationContext)
        }

        val notification =
            NotificationCompat.Builder(applicationContext, NotificationUtil.CHANNEL_TEMPERATURESYNC)
                .setSmallIcon(R.drawable.ic_hue_motionsensor)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_delete, cancel, intent)
                .build()

        return ForegroundInfo(10, notification)
    }

    enum class SyncState {
        START, BRIDGE, SENSOR, DONE
    }
}