package com.example.shykoe.band

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import java.util.*
import android.support.v4.content.LocalBroadcastManager
import com.example.shykoe.band.UartService.LocalBinder
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGatt








class UartService : Service() {
    private val STATE_DISCONNECTED = 0
    private val STATE_CONNECTING = 1
    private val STATE_CONNECTED = 2
    private val TAG = UartService::class.java.simpleName
    private var mBluetoothManager: BluetoothManager? = null
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothDeviceAddress: String? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    private var mConnectionState = STATE_DISCONNECTED
    private val mBinder = LocalBinder()
    val ACTION_GATT_CONNECTED = "com.hch.ble.ACTION_GATT_CONNECTED"
    val ACTION_GATT_DISCONNECTED = "com.hch.ble.ACTION_GATT_DISCONNECTED"
    val ACTION_GATT_SERVICES_DISCOVERED = "com.hch.ble.ACTION_GATT_SERVICES_DISCOVERED"
    val ACTION_DATA_AVAILABLE = "com.hch.ble.ACTION_DATA_AVAILABLE"
    val EXTRA_DATA = "com.hch.ble.EXTRA_DATA"
    val DEVICE_DOES_NOT_SUPPORT = "com.hch.ble.DEVICE_DOES_NOT_SUPPORT"

    val TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb")
    val TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb")
    val CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    val FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb")
    val DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb")
    val RX_SERVICE_UUID = UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb")
    val RX_CHAR_UUID = UUID.fromString("0000fff2-0000-1000-8000-00805f9b34fb")
    val TX_CHAR_UUID = UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb")
    val HR_SERVICE_UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    val HR_CHAR_UUID = UUID.fromString("00002a38-0000-1000-8000-00805f9b34fb")

    fun isConnected(): Boolean {
        return mConnectionState == STATE_CONNECTED
    }
    val mGattCallback : BluetoothGattCallback = object: BluetoothGattCallback(){
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            super.onConnectionStateChange(gatt, status, newState)
            var intentAction : String
            if(newState == BluetoothProfile.STATE_CONNECTED){
                intentAction = ACTION_GATT_CONNECTED
                mConnectionState = STATE_CONNECTED
                broadcastUpdate(intentAction)
                Log.i(TAG,"Connected to GATT server.")
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt!!.discoverServices())
            }else if(newState == BluetoothProfile.STATE_DISCONNECTED){
                intentAction = ACTION_GATT_DISCONNECTED
                mConnectionState = STATE_DISCONNECTED
                Log.i(TAG, "Disconnected from GATT server.")
                broadcastUpdate(intentAction)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            super.onServicesDiscovered(gatt, status)
            if(status == BluetoothGatt.GATT_SUCCESS){
                Log.w(TAG, "mBluetoothGatt = " + mBluetoothGatt )
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED)
            }else{
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt?, characteristic: BluetoothGattCharacteristic?, status: Int) {
            super.onCharacteristicRead(gatt, characteristic, status)
            if(status == BluetoothGatt.GATT_SUCCESS){
                if(HR_CHAR_UUID == characteristic!!.uuid ){
                    broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic)
                }
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                    characteristic: BluetoothGattCharacteristic) {
            if (HR_CHAR_UUID==characteristic.uuid) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            } else if (TX_CHAR_UUID==characteristic.uuid) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }
    }

    inner class LocalBinder : Binder() {
        internal val service: UartService
            get() = this@UartService
    }
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }
    private fun broadcastUpdate(action: String, characteristic: BluetoothGattCharacteristic) {
        val intent = Intent(action)
        if (HR_CHAR_UUID == characteristic.uuid ) {

            // Log.d(TAG, String.format("Received HR: %d",characteristic.getValue() ));
            intent.putExtra(EXTRA_DATA, characteristic.value);
        } else if(HR_CHAR_UUID== characteristic.uuid ) {
            // Log.d(TAG, String.format("Received Tx: %d",characteristic.getValue() ));
            intent.putExtra(EXTRA_DATA, characteristic.value);
        } else if (TX_CHAR_UUID == characteristic.uuid ) {
            intent.putExtra(EXTRA_DATA, characteristic.value);
        } else {

        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    override fun onBind(p0: Intent?): IBinder {
        return mBinder
    }

}