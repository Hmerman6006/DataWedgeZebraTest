package co.za.dataleaf.datawedgezebratest

interface OnReceiverListenerInterface {
    fun onReceivingScannerStatusBroadcast(status: String)

    var dwReceiverActive: Boolean
}