package co.za.dataleaf.datawedgezebratest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

class DwReceiver : BroadcastReceiver() {
    private var listener: OnReceiverListenerInterface? = null

    fun setOnReceiverListenerInterface(context: Context) {
        this.listener = context as OnReceiverListenerInterface
    }

    private fun setActive(a: Boolean) {
        Log.d("DwReceiver", "$a")
        this.listener?.apply { dwReceiverActive = a }
    }

    override fun onReceive(context: Context, intent: Intent) {
        //  This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        //  Notify registered observers
//        ObservableObject.instance.updateValue(intent)
        Log.d("DwReceiver", "Logging")
        if (intent.action.equals(DwInterface.NOTIFICATION_ACTION)) {

            if(intent.hasExtra(DwInterface.DATAWEDGE_NOTIFICATION)) {
                val b: Bundle? = intent.getBundleExtra(DwInterface.DATAWEDGE_NOTIFICATION)
                val t: String? = b?.getString(DwInterface.DATAWEDGE_NOTIFICATION_TYPE)
                if (t != null) {
                    when(t) {
                        DwInterface.NOTIFICATION_TYPE_SCANNER_STATUS -> {
                            Log.d("DwReceiver", "NOTIFICATION_TYPE_SCANNER_STATUS")
                            val scannerStatus = b.getString("STATUS")
                            Log.d("DwReceiver", "STATUS: ${b.getString("STATUS")} PROFILE: ${b.getString("PROFILE_NAME")}")
                            if (scannerStatus != null) {
                                setActive(true)
                                this.listener?.apply {
                                    onReceivingScannerStatusBroadcast(scannerStatus)
                                }
                            }
                        }
                        DwInterface.NOTIFICATION_TYPE_PROFILE_SWITCH -> {
                            Log.d("DwReceiver", "NOTIFICATION_TYPE_PROFILE_SWITCH")
                            Log.d("DwReceiver", "ENABLED: ${b.getString("PROFILE_ENABLED")} PROFILE: ${b.getString("PROFILE_NAME")}")

                        }
                        DwInterface.NOTIFICATION_TYPE_CONFIGURATION_UPDATE -> {
                            Log.d("DwReceiver", "NOTIFICATION_TYPE_CONFIGURATION_UPDATE")
                            Log.d("DwReceiver", "STATUS: $b")
                        }
                        else -> Log.d("DwReceiver", "unknown $t")
                    }
                }
            }
//            if (intent.hasExtra("com.symbol.datawedge.api.RESULT_SCANNER_STATUS")) {
//                val scannerStatus =
//                    intent.getStringExtra("com.symbol.datawedge.api.RESULT_SCANNER_STATUS")
//                Log.d("DwReceiver", "Scanner status:$scannerStatus")
//                if (scannerStatus != null) {
//                    this.listener?.apply {
//                        onReceivingScannerStatusBroadcast(scannerStatus)
//                    }
//                }
//            }
        } else setActive(false)
        Log.d("DwReceiver", "Logging after")
    }
}