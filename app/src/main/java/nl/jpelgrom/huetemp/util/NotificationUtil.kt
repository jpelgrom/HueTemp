package nl.jpelgrom.huetemp.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import nl.jpelgrom.huetemp.R


class NotificationUtil {
    fun createChannelTemperatureSync(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                CHANNEL_TEMPERATURESYNC,
                context.getString(R.string.notifications_channel_temperaturesync_title),
                NotificationManager.IMPORTANCE_LOW
            )
            channel.enableLights(false)
            channel.setShowBadge(false)
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_TEMPERATURESYNC = "notifications.temperaturesync"
    }
}