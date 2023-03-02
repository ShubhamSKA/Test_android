package com.purdue.milestonebleproject

import android.annotation.SuppressLint
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.system.Os.close
import android.util.Log
import java.util.*


/**
 * BluetoothLeService.kt
 * THIS IS FOR STUDENT USES. PLEASE REFER PROVIDED STEP-BY-STEP MANUAL TO COMPLETE THE CODE.
 *
 * @author Shubham Saluja Kumar Agarwal (skumarag@purdue.edu)
 */

@SuppressLint("MissingPermission")
class BluetoothLeService {

    // Companion Object for storing GATT Attributes
    companion object {
        // Connection States
        val STATE_DISCONNECTED = 0
        val STATE_CONNECTING = 1
        val STATE_CONNECTED = 2

        // Current Connection State
        var connectionState = STATE_DISCONNECTED

        // GATT Server Attributes
        val ACTION_GATT_CONNECTED = "ACTION_GATT_CONNECTED"
        val ACTION_GATT_DISCONNECTED = "ACTION_GATT_DISCONNECTED"
        val ACTION_GATT_SERVICE_DISCOVERED = "ACTION_GATT_SERVICE_DISCOVERED"
        val ACTION_DATA_AVAILABLE = "ACTION_DATA_AVAILABLE"
        val EXTRA_DATA = "EXTRA_DATA"

        // Get Read Attribute (RxCharacteristics)
        val UUID_DATA_RECEIVE = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")
    }

    // BluetoothGatt Service Object
    private var bluetoothGatt: BluetoothGatt? = null

    // Bluetooth Gatt Callback Object
    private val bluetoothGattCallback = object : BluetoothGattCallBack() {
        override fun onConnectionStateChanged(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BluetoothGattCallback", "Successfully connected to the GATT Server")

                if (gatt != null) {
                    connectionState = STATE_CONNECTED

                    // Start discover services. (We will come back here later.)
                    bluetoothGatt!!.discoverService()
                    sendServiceStatusUpdate(ACTION_GATT_CONNECTED)
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BluetoothGattCallback", "Disconnected from GATT server")
                connectionState = STATE_DISCONNECTED

                // Update service status (We will come back here later.)
                sendServiceStatusUpdate(ACTION_GATT_DISCONNECTED)
            }
        }
        override fun onServiceDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BluetoothGattCallback", "Service discovered!")

                // Update service status (We will come back here later.)
                sendServiceStatusUpdate(ACTION_GATT_SERVICE_DISCOVERED)
            } else {
                Log.d("BluetoothGattCallback", "onServiceDiscovered receives $status")

            }
        }
        override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            sendServiceStatusUpdate(ACTION_DATA_AVAILABLE, characteristic)
        }
    }
    // Bluetooth Adapter
    private lateinit var bluetoothAdapter: BluetoothAdapter

    // Local Binder Object
    private val binder = LocalBinder()
    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    override fun onUnbind(intent: Intent?): Boolean) {
        close()
        super.onUnbind(intent)
    }

    // Required implementation to inherit Service() class.

    // Inner class for Access Binder Locally
    inner class LocalBinder : Binder() {
        // function that instaitiate this BluetoothLeService object LOCALLY.
        fun getService() : BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    // Initialize Bluetooth Adapter
    fun initialize(): Boolean {
        if (bluetoothAdapter == null) {
            // Print error message on logcat
            Log.d("BluetoothScanActivity", "Unable to obtain a BluetoothAdapter")
            return false
        } else {
            return true
        }
    }


    // Function that connect selected BLE Device using its address.

    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // Connect to Gatt server on this device (we will come back here later.)
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                return true;
                return true
            } catch (e: IllegalArgumentException) {
                Log.w("BluetoothLeService", "Device not found with provided address. Unable to connect.")
                return false
            }
        }
    }

    // Broadcast sender object (Broadcast Gatt Service Status)
    private fun sendServiceStatusUpdate(action: String) {
        val intent = Intent(action)
        when (characteristic.uuid) {
            UUID_DATA_RECEIVE -> {
                // This is the point where inbound data from an external device arrives first.
                val data: ByteArray? = characteristic.value
                if (data != null && data.isNotEmpty()) {
                    intent.putExtra(EXTRA_DATA, data.toString(Charsets.UTF_8))
                }
            }
        }
        sendBroadcast(intent)
    }

    // Overloaded Broadcast Sender function for data transfer.
    fun getSupportedGattServices(): List<BluetoothGattService?>? {
        return bluetoothGatt?.services
    }

    // Retrieve all available services in connected device.


    // function for notifying Gatt Server a change of characteristic.
    fun setCharacteristicNotification(characteristic: BluetoothGattCharacteristic, enabled: Boolean) {
        if (bluetoothAdapter == null || bluetoothGatt == null {
                Log.d("BluetoothLeService", "BluetoothAdapter has not been initialized")
                return
            }

            bluetoothGatt!!.setChracteristicNotification(characteristic, true)

        if (UUID_DATA_RECEIVE == characteristic.uuid) {
            val descriptor: BluetoothGattDescriptor = characteristic.getDescriptor(
                UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
            )
            descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            bluetoothGatt!!.writeDescriptor(descriptor)
        }
    }

    // Function to close GATT Server connection once the application finishes use it.
    private fun close() {
        bluetoothGatt?.let {
            it.close()
            bluetoothGatt = null
        }
    }

}