package com.example.appa.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.BroadcastReceiver;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appa.R;


public class BluetoothHandler {

    BluetoothAdapter btAdapter;
    TextView mStatusBT;
    ImageView btStatusIcon;
    Button mEnabledBT;
    BroadcastReceiver mReceiver;

    public BluetoothHandler()
    {
        //Bluetooth setup
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check if Bluetooth is available on this device
        // If available, perform all necessary setup actions
        // Else, notify user that this device does not support BT
        if (btAdapter == null) {
            //mStatusBT.setText("Bluetooth is unavailable on this device");
        }
        else
        {
            //init();
        }
    }
    public void init()
    {
        /*if(!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }*/
    }

    public void btUpdateStatusIcon()
    {
        if(!btAdapter.isEnabled())
        {
            //btStatusIcon.setImageResource(R.drawable.ic_action_bluetooth_on);
        }
        else
        {
            //btStatusIcon.setImageResource(R.drawable.ic_action_bluetooth_off);
        }
    }

    public boolean btAdapterAvailable()
    {
        return btAdapter != null;
    }

    public boolean btAdapterEnabled()
    {
        if(btAdapterAvailable()){
            return btAdapter.isEnabled();
        }
        return false;

    }

    public boolean btDeviceConnected()
    {
    return false;
        //return btAdapter.getBondedDevices().contains();
    }
    public BluetoothAdapter getBtAdapter() {
        return btAdapter;
    }

    public void setBtAdapter(BluetoothAdapter btAdapter) {
        this.btAdapter = btAdapter;
    }

    public BroadcastReceiver getReceiver()
    {
        return mReceiver;
    }
}
