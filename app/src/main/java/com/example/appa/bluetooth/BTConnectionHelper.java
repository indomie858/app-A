package com.example.appa.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.appa.bluetooth.message.MessageHandler;
import com.example.appa.ui.BluetoothDialog;

import java.util.ArrayList;

public class BTConnectionHelper {
    // This class serves as a helper/utility class
    // Which handles the details of querying and conecting to Bluetooth.

    BluetoothServiceHandler mBTServiceHandler;
    BluetoothHandler mBluetoothHandler;
    public Context mContext; // Should be context from MainActivity
    boolean found = false; // Flagged true as soon as the first discoverable APP-A device is seen

    public BTConnectionHelper(BluetoothServiceHandler bluetoothServiceHandler, Context context) {
        mBTServiceHandler = bluetoothServiceHandler;
        mBTServiceHandler.setMessageHandler(new MessageHandler(mMessageHandler));
        mBluetoothHandler = bluetoothServiceHandler.getBTHandler();
        mContext = context;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);
    }


    // Acts as a listener for device discovery intents
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if(!found) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String deviceName = device.getName();
                    String deviceAddr = device.getAddress();
                    // Keeping the default HC-05 name here for now,
                    // for dev purposes,
                    // though we should get rid of this in production.
                    if(deviceName!= null && deviceAddr != null && (deviceName.equals("APP-A") || deviceName.equals("DSD TECH"))) {
                        mBTServiceHandler.connectTo(deviceAddr);
                    }
                }
            }
        }
    };

    public void appaConnect() {
        mBluetoothHandler.startDiscover();
    }

    private Handler mMessageHandler = new Handler() {
        String LOG_TAG = BluetoothDialog.class.getSimpleName();
        String mostRecentRead;
        @Override
        public void handleMessage(Message message)
        {
            switch (message.what) {
                case 0: //send bytes to device
                    break;
                case 1: //read line from device
                    String line = (String) message.obj;
                    mostRecentRead = line;
                    Log.e(LOG_TAG, mostRecentRead);
                    break;
                case 2: //not connected

                    break;
                case 3: //connection failed
                    Log.e(LOG_TAG, "Failed to establish connection to device.");
                    break;
                case 4: //connection lost
                    Log.e(LOG_TAG, "Lost connection to device");
                    break;
                case 5: //connecting to device
                    Log.e(LOG_TAG, "Establishing connection to device.");
                    //btArrayAdapter.getView(lastconnectedbtindex, null, this);
                    break;
                case 6: //connection to device successful
                    Log.e(LOG_TAG, "Connection to device successful");
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + message.what);
            }
        }
    };
}
