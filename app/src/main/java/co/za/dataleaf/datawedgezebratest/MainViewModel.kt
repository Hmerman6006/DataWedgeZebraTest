package co.za.dataleaf.datawedgezebratest

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ScannerStatus {
    WAITING,
    SCANNING,
    CONNECTED,
    DISCONNECTED,
    IDLE,
    DISABLED,
    UNKNOWN
}
class MainViewModel: ViewModel() {
    private val _unknown = "BUSY"
    private val _status = MutableStateFlow<String>(_unknown)
    val status = _status.asStateFlow()

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
        if (s.isNullOrBlank()) _status.value = _unknown
        else _status.value = s
    }
}