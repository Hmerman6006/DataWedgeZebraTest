package co.za.dataleaf.datawedgezebratest

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import co.za.dataleaf.datawedgezebratest.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), OnReceiverListenerInterface {
    private lateinit var binding: ActivityMainBinding
    private lateinit var dialog: ShowRoundDialogFragment
    private val dwInterface = DwInterface()
    private val receiver = DwReceiver()
    override var dwReceiverActive: Boolean = false

    private val viewModel: MainViewModel by viewModels()

    companion object {
        val TAG = MainActivity::class.simpleName
        const val PROFILE_NAME = "DataWedgeKotlinDemo"
        const val PROFILE_INTENT_ACTION = "co.za.dataleaf.datawedgezebratest.SCAN"
        const val PROFILE_INTENT_START_ACTIVITY = "0"
        const val SCAN_HISTORY_FILE_NAME = "ScanHistory"
        const val LOADER = "bottom_sheet_dialog"
    }

    override fun onReceivingScannerStatusBroadcast(status: String) {
        Log.v(TAG, "RECEIVING: $status")
        viewModel.setStatus(status)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DwUtilities.CreateDWProfile(this)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.status.collectLatest {

                    binding.tvScannerStatus.text = it
                    Log.i(TAG, viewModel.isDwScannerUnavailable().toString())
                    Log.i(TAG, viewModel.getStatus().toString())
                    Log.i(TAG, "${(!dwReceiverActive)}  ${(viewModel.getStatus() == ScannerStatus.UNKNOWN)}")
                    if (!viewModel.isDwScannerUnavailable()) {
                        viewModel.reactToInactiveDwScanner()
                    }
                }
            }
        }
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.dwScannerActive.collectLatest {
                    Log.i(TAG, viewModel.getStatus().toString())
                    if(!it) {
                        setDialog()
//                        Snackbar.make(
//                            this@MainActivity,
//                            binding.root,
//                            "No datawedge scanner service encountered. Do you want to switch to camera scanner?",
//                            Snackbar.LENGTH_INDEFINITE
//                        ).setAction("Yes") {
//                            Log.i(TAG, "Set camera as default scanner ...")
//                        }.show()
                    }
                }
            }
        }
    }

    private fun setDialog() {
        if(::dialog.isInitialized) {
            val s = dialog.dialog?.isShowing == true
            if (!dialog.isAdded) {
                dialog.show(supportFragmentManager, LOADER)
                return
            }
            if (!s) dialog.dialog?.show()
        } else {
            dialog = DialogHelper.bottomShowRoundDialogBuild()
            dialog.show(supportFragmentManager, LOADER)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent?.hasExtra(DwInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING) == true) {
            //  Handle scan intent received from DataWedge, add it to the list of scans
            var scanData =
                intent.getStringExtra(resources.getString(R.string.datawedge_intent_key_data))  //  intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_DATA_STRING)
            var symbology =
                intent.getStringExtra(resources.getString(R.string.datawedge_intent_key_label_type))  //   intent.getStringExtra(DWInterface.DATAWEDGE_SCAN_EXTRA_LABEL_TYPE)
            Log.d(TAG, "Logging $symbology $scanData")
            Log.d(TAG,
                intent.getStringExtra(resources.getString(R.string.datawedge_intent_key_source))
                    .toString()
            )
            Log.d(TAG, scanData.toString())
            Log.d(TAG, symbology.toString())
            scanData?.let {
                binding.tvBarcode.text = it
            }
            Log.d(TAG, "Logging after")
        } else Log.d(TAG, "No extra after")

    }

    private fun scannerStatus() {
        val b = Bundle()
        b.putString("com.symbol.datawedge.api.APPLICATION_NAME", "co.za.dataleaf.datawedgezebratest")
        b.putString("com.symbol.datawedge.api.NOTIFICATION_TYPE", "SCANNER_STATUS")
        val i = Intent()
        i.action = DwInterface.DATAWEDGE_SEND_ACTION
        i.putExtra("com.symbol.datawedge.api.REGISTER_FOR_NOTIFICATION", b)
        this.sendBroadcast(i)
        Log.d("scannerStatus", "Logging 2")
//        val i = Intent()
//        i.setAction(DwInterface.DATAWEDGE_SEND_ACTION);
//        i.putExtra(DwInterface.DATAWEDGE_SCANNER_GET_STATUS,"");
//        i.putExtra(DwInterface.DATAWEDGE_EXTRA_SEND_RESULT,"true");
//        i.putExtra("com.symbol.datawedge.api.RESULT_CATEGORY","android.intent.category.DEFAULT");
//        this.sendBroadcast(i);
    }

    private fun registerScannerReceiver() {
        receiver.setOnReceiverListenerInterface(this)
        //  Register broadcast receiver to listen for responses from DW API
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.symbol.datawedge.api.NOTIFICATION_ACTION")  //DwInterface.DATAWEDGE_RETURN_ACTION)
//        intentFilter.addCategory(DwInterface.DATAWEDGE_RETURN_CATEGORY)
        registerReceiver(receiver, intentFilter)
        scannerStatus()
    }

    private fun unRegisterScannerReceiver() {
        // To unregister, change only the iPutExtra command
        val bb = Bundle()
        bb.putString("com.symbol.datawedge.api.APPLICATION_NAME", "co.za.dataleaf.datawedgezebratest")
        bb.putString("com.symbol.datawedge.api.NOTIFICATION_TYPE", "SCANNER_STATUS")
        val ii = Intent()
        ii.action = "com.symbol.datawedge.api.ACTION"
        ii.putExtra("com.symbol.datawedge.api.UNREGISTER_FOR_NOTIFICATION", bb)
        this.sendBroadcast(ii)
        unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()
        registerScannerReceiver()
    }

    override fun onStop() {
        super.onStop()
        unRegisterScannerReceiver()
    }

    private fun createDataWedgeProfile() {
        //  Create and configure the DataWedge profile associated with this application
        //  For readability's sake, I have not defined each of the keys in the DWInterface file
        dwInterface.sendCommandString(this, DwInterface.DATAWEDGE_SEND_CREATE_PROFILE, PROFILE_NAME)
        val profileConfig = Bundle()
        profileConfig.putString("PROFILE_NAME", PROFILE_NAME)
        profileConfig.putString("PROFILE_ENABLED", "true") //  These are all strings
        profileConfig.putString("CONFIG_MODE", "UPDATE")
        val barcodeConfig = Bundle()
        barcodeConfig.putString("PLUGIN_NAME", "BARCODE")
        barcodeConfig.putString("RESET_CONFIG", "true") //  This is the default but never hurts to specify
        val barcodeProps = Bundle()
        barcodeConfig.putBundle("PARAM_LIST", barcodeProps)
        profileConfig.putBundle("PLUGIN_CONFIG", barcodeConfig)
        val appConfig = Bundle()
        appConfig.putString("PACKAGE_NAME", packageName)      //  Associate the profile with this app
        appConfig.putStringArray("ACTIVITY_LIST", arrayOf("*"))
        profileConfig.putParcelableArray("APP_LIST", arrayOf(appConfig))
        dwInterface.sendCommandBundle(this, DwInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)
        //  You can only configure one plugin at a time in some versions of DW, now do the intent output
        profileConfig.remove("PLUGIN_CONFIG")
        val intentConfig = Bundle()
        intentConfig.putString("PLUGIN_NAME", "INTENT")
        intentConfig.putString("RESET_CONFIG", "true")
        val intentProps = Bundle()
        intentProps.putString("intent_output_enabled", "true")
        intentProps.putString("intent_action", PROFILE_INTENT_ACTION)
        intentProps.putString("intent_delivery", PROFILE_INTENT_START_ACTIVITY)  //  "0"
        intentConfig.putBundle("PARAM_LIST", intentProps)
        profileConfig.putBundle("PLUGIN_CONFIG", intentConfig)
        dwInterface.sendCommandBundle(this, DwInterface.DATAWEDGE_SEND_SET_CONFIG, profileConfig)
    }
}