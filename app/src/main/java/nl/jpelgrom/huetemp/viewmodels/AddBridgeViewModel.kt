package nl.jpelgrom.huetemp.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import nl.jpelgrom.huetemp.repositories.DiscoveryRepository

class AddBridgeViewModel : ViewModel() {
    enum class CurrentState {
        SEARCHING,
        PUSHLINK,
        CONNECTED
    }

    private val repository = DiscoveryRepository()

    val state: LiveData<CurrentState> = MutableLiveData(CurrentState.SEARCHING)

    val foundBridges = liveData {
        emit(repository.searchForBridges())
    }
}