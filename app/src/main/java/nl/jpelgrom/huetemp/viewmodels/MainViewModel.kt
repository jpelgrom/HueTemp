package nl.jpelgrom.huetemp.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import nl.jpelgrom.huetemp.service.TemperatureUpdatesWorker

class MainViewModel : ViewModel() {
    fun runWorker(context: Context?) {
        if (context != null) {
            WorkManager.getInstance(context)
                .enqueue(OneTimeWorkRequestBuilder<TemperatureUpdatesWorker>().build())
        }
    }
}