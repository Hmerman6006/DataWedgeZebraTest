package co.za.dataleaf.datawedgezebratest

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class ScannerStatus {
    WAITING,
    SCANNING,
    CONNECTED,
    DISCONNECTED,
    IDLE,
    DISABLED,
    UNKNOWN
}
class MainViewModel() : ViewModel(), OnReceiverListenerInterface {
    private val _unknown = "NO SCANNER"
    private val _status = MutableStateFlow<String>(_unknown)
    val status = _status.asStateFlow()
    private var _dwScannerActive = MutableStateFlow<Boolean>(true)
    val dwScannerActive = _dwScannerActive.asStateFlow()
    override fun onReceivingScannerStatusBroadcast(status: String) {
        TODO("Not yet implemented")
    }

    override var dwReceiverActive: Boolean = false

    fun getStatus(): ScannerStatus {
        return if(_status.value == "WAITING") {
            ScannerStatus.WAITING
        } else if(_status.value == "SCANNING") {
            ScannerStatus.SCANNING
        } else if(_status.value == "CONNECTED") {
            ScannerStatus.CONNECTED
        } else if(_status.value == "DISCONNECTED") {
            ScannerStatus.DISCONNECTED
        } else if(_status.value == "IDLE") {
            ScannerStatus.IDLE
        } else if(_status.value == "DISABLED") {
            ScannerStatus.DISABLED
        } else {
            ScannerStatus.UNKNOWN
        }
    }

    fun setStatus(s: String) {
        if (s.isBlank()) _status.value = _unknown
        else _status.value = s
    }

    fun reactToInactiveDwScanner() {
        Log.i("MAINVIEWMODEL", "ACTIVE: ${isDwScannerUnavailable()}")
        viewModelScope.launch {
            for(i in 1..5) {
                if(isDwScannerUnavailable()) break
                delay(1000L)
                Log.i("MAINVIEWMODEL", "ACTIVE: ${isDwScannerUnavailable()} $i")
            }
            Log.i("MAINVIEWMODEL", "ACTIVE: ${isDwScannerUnavailable()}")
           _dwScannerActive.value = isDwScannerUnavailable()
        }
    }

    fun isDwScannerUnavailable(): Boolean {
        return (((getStatus() != ScannerStatus.UNKNOWN) && dwReceiverActive))
    }
}