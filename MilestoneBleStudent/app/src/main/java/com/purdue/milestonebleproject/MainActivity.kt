package com.purdue.milestonebleproject

import android.content.*
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.Manifest
import android.os.IBinder
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat

/**
 * MainActivity
 * THIS IS FOR STUDENT USES. PLEASE REFER PROVIDED STEP-BY-STEP MANUAL TO COMPLETE THE CODE.
 *
 * @author Shubham Saluja Kumar Agarwal (skumarag@purdue.edu)
 */

class MainActivity : AppCompatActivity() {

    // Permission Checker
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("PermissionLauncher", "Permission Granted")
        } else {
            Toast.makeText(this, "Permission must be granted", Toast.LENGTH_LONG).show()
        }
    }

    // List of required permissions
    private val requriedPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_BACKGROUND_LOCATION
    )

    // Device Info
    private var deviceName: String? = null
    private var deviceAddress: String? = null

    // Service Connection Status
    private var isConnected = false

    // Binding Service
    private var mBluetoothService : BluetoothLeService? = null
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName?, service: IBinder?) {
            mBluetoothService = (service as BluetoothLeService.LocalBinder).getService()
            mBluetoothService?.let { bluetooth ->
                if (!bluetooth.initialize()) {
                    // BLUETOOTH is not available in this device.
                    Log.d("Error: ", "Unable to initialize Bluetooth")
                    finish()        // close application.
                }
                mBluetoothLeService!!.connect(deviceAddress)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE is not available on your device", Toast.LENGTH_LONG).show()
            finish()
        }

        // Ask user to get necessary permission
        for (permission in requriedPermissions) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(permission)
            }
        }

        // Check selected device information
        deviceName = intent.getStringExtra("DEVICE_NAME")
        deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")

        if (deviceName != null && deviceAddress != null) {
            val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
            bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        }

        // ----------------------------------------------------------------------------------------
        // DO NOT MODIFY THIS CODE
        findViewById<Button>(R.id.searchDeviceBtn).setOnClickListener {
            startActivity(Intent(this, BluetoothScanActivity::class.java))
        }
        // ----------------------------------------------------------------------------------------
    }

    // Broadcast Receiver
    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context, p1: Intent) {
            when (p1.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    isConnected = true
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    isConnected = false
                }
                BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERED -> {
                    for (service in mBluetoothLeService!!.getSupportedServices()!!) {
                        var characteristic = service?.getCharacteristic(
                            BluetoothLeService.UUID_DATA_RECEIVE
                        )
                        if (characteristic != null) {
                            mBluetoothLeService!!.setCharacteristicNotification(characteristic, true)
                        }
                    }
                    BluetoothLeService.ACTION_DATA_AVAILABLE {
                        val data = p1.getStringExtra(BluetoothLeService.EXTRA_DATA)
                        // Test code for debug (you can see the data received.
                        Log.d("gattUpdateReciever", "Received Data $data")

                        // This 'data' is the data sent from an external device through BLE communication.
                        // you can use this data to update your chart or other necessary action based on your
                        // final project.

                        // -----------------------------------------------------------------------------------
                        // Start write your code here.







                        // -----------------------------------------------------------------------------------
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (intent.hasExtra("DEVICE_ADDRESS")) {
            deviceAddress = intent.getStringExtra("DEVICE_ADDRESS")
        }
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)

    }


    // Gatt Service Intent Filter
    private fun makeGattUpdateIntentFilter(): IntentFilter? {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_SERVICE_DISCOVERED)
            addAction(BluetoothLeService.ACTION_DATA_AVAILABLE)
        }
    }

}