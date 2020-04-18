package nl.jpelgrom.huetemp.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import androidx.work.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.jpelgrom.huetemp.data.DiscoveredBridge
import nl.jpelgrom.huetemp.data.HueBridgeType
import nl.jpelgrom.huetemp.repositories.DiscoveryRepository
import nl.jpelgrom.huetemp.service.TemperatureUpdatesWorker
import java.util.concurrent.TimeUnit

class AddBridgeViewModel : ViewModel() {
    enum class CurrentState {
        SEARCHING,
        PUSHLINK,
        CONNECTED,
        CLOSE
    }

    private val repository = DiscoveryRepository()

    val currentState: MutableLiveData<CurrentState> = MutableLiveData(CurrentState.SEARCHING)

    val foundBridges = liveData { // TODO run again on state change
        emit(repository.searchForBridges())
    }

    fun startPushlink(bridge: DiscoveredBridge, context: Context?) {
        // TODO UI for manual IP + check if there is a bridge there
        if (bridge.type == HueBridgeType.IP) {
            return
        }

        currentState.value = CurrentState.PUSHLINK

        viewModelScope.launch {
            var tries = 0
            repository.prepareLoginToBridge(bridge)
            while (currentState.value == CurrentState.PUSHLINK && tries < 30) {
                when (repository.loginToBridge(bridge, context)) {
                    DiscoveryRepository.BridgeLoginTry.SUCCESS -> currentState.value =
                        CurrentState.CONNECTED
                    DiscoveryRepository.BridgeLoginTry.REJECTED -> tries++
                    else -> currentState.value = CurrentState.SEARCHING // TODO hard failure
                }
            }
            if (currentState.value == CurrentState.PUSHLINK) {
                cancelPushlink() // Must've reached the limit
            } else if (currentState.value == CurrentState.CONNECTED) {
                if (context != null) {
                    val constraints =
                        Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                    val updaterRequest =
                        PeriodicWorkRequestBuilder<TemperatureUpdatesWorker>(15, TimeUnit.MINUTES)
                            .setConstraints(constraints)
                            .build()
                    WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                        "temperatureUpdatesWorker",
                        ExistingPeriodicWorkPolicy.REPLACE,
                        updaterRequest
                    )
                }

                delay(3_000)
                currentState.value = CurrentState.CLOSE
            } // else SEARCHING, user cancelled
        }
    }

    fun cancelPushlink() {
        currentState.value = CurrentState.SEARCHING
    }
}