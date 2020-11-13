package com.example.appa.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.appa.bluetooth.message.MessageHandler;

import java.util.Set;

public class BluetoothServiceHandler extends Service {
    public static final String LOG_TAG = BluetoothServiceHandler.class.getName();

    private final IBinder binder = new LocalBinder();
    private BluetoothHandler bluetoothHandler;

    public class LocalBinder extends Binder {
        public BluetoothServiceHandler getService() {
            return BluetoothServiceHandler.this;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(LOG_TAG, "AYYYY WE DID IT");
        bluetoothHandler = new BluetoothHandler(null, null);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public BluetoothHandler getBTHandler() {
        return bluetoothHandler;
    }

    public void setMessageHandler(MessageHandler mHandler) {
        bluetoothHandler.setMessageHandler(mHandler);
    }

    public void connectTo(String address) {
        bluetoothHandler.setAddress(address);
        bluetoothHandler.connect();
    }

    public void disconnect() {
        bluetoothHandler.disconnect();
    }

    public Set<BluetoothDevice> getBluetoothDevices() {
        return bluetoothHandler.getDevices();
    }

    public void writeToDevice(byte[] bytes)
    {
        bluetoothHandler.getConnectedDeviceThread().writeToDevice(bytes);
    }
}
