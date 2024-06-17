package com.mastermovilesua.runtrackerraul.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.ParcelUuid
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

class BleManager(context: Context) {

    private val bluetoothAdapter: BluetoothAdapter
    private var bluetoothGatt: BluetoothGatt? = null
    private var ecgCharacteristic: BluetoothGattCharacteristic? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isScanning = false
    private val _scanResults = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scanResults: StateFlow<List<BluetoothDevice>> get() = _scanResults

    @SuppressLint("MissingPermission")
    fun startScan() {
        if (isScanning) return

        isScanning = true
        val scanFilter = ScanFilter.Builder()
            .setServiceUuid(ParcelUuid(UUID.fromString(ECG_SERVICE_UUID)))
            .build()
        val scanSettings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val filters = listOf(scanFilter)
        bluetoothAdapter.bluetoothLeScanner.startScan(null, scanSettings, leScanCallback)
        handler.postDelayed({
            stopScan()
        }, SCAN_PERIOD)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (!isScanning) return

        isScanning = false
        bluetoothAdapter.bluetoothLeScanner.stopScan(leScanCallback)
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(context: Context?, deviceAddress: String?) {
        val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
    }

    private val leScanCallback = object : ScanCallback() {
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            val updatedList = _scanResults.value.toMutableList()
            if (!updatedList.contains(device)) {
                updatedList.add(device)
                _scanResults.value = updatedList
                Log.i(TAG, "Device found: ${device.address} - ${device.name}")
            }
        }

        override fun onScanFailed(errorCode: Int) {
            Log.e(TAG, "Scan failed with error: $errorCode")
        }
    }

    private val gattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT server.")
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT server.")
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val ecgService = gatt.getService(UUID.fromString(ECG_SERVICE_UUID))
                if (ecgService != null) {
                    ecgCharacteristic = ecgService.getCharacteristic(
                        UUID.fromString(ECG_CHARACTERISTIC_UUID)
                    )
                    if (ecgCharacteristic != null) {
                        gatt.setCharacteristicNotification(ecgCharacteristic, true)
                        val descriptor = ecgCharacteristic!!.getDescriptor(
                            CLIENT_CHARACTERISTIC_CONFIG_UUID
                        )
                        descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                        gatt.writeDescriptor(descriptor)
                    }
                }
            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            if (characteristic.uuid == UUID.fromString(ECG_CHARACTERISTIC_UUID)) {
                val ecgData = characteristic.value
                // Handle ECG data here
                Log.i(TAG, "Received ECG data: " + bytesToHex(ecgData))
            }
        }
    }

    init {
        val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothAdapter = bluetoothManager.adapter
    }

    private fun bytesToHex(bytes: ByteArray): String {
        val hexArray = "0123456789ABCDEF".toCharArray()
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = hexArray[v ushr 4]
            hexChars[j * 2 + 1] = hexArray[v and 0x0F]
        }
        return String(hexChars)
    }

    companion object {
        private const val TAG = "BleManager"
        private const val ECG_SERVICE_UUID = "0000180D-0000-1000-8000-00805f9b34fb"
        private const val ECG_CHARACTERISTIC_UUID = "00002a37-0000-1000-8000-00805f9b34fb"
        private val CLIENT_CHARACTERISTIC_CONFIG_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val SCAN_PERIOD: Long = 10000 // 10 seconds
    }
}
