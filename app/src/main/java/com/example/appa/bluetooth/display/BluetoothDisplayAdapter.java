package com.example.appa.bluetooth.display;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.appa.R;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDisplayAdapter extends ArrayAdapter<BluetoothDevice> {
    private static final String LOG_TAG = BluetoothDisplayAdapter.class.getSimpleName();

    public BluetoothDisplayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    /*
    *
    * @param context Current context. Used to inflate layout file
    * @param btDevices Bluetooth Devices to display in a list
     */
    public BluetoothDisplayAdapter(Activity context, ArrayList<BluetoothDevice> btDevices) {
        super(context, 0, btDevices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       View listItemView = convertView;
       if(listItemView == null) {
           listItemView = LayoutInflater.from(getContext()).inflate(R.layout.btdevice_list_item, parent, false);
       }

       BluetoothDevice currentBTDevice = getItem(position);

       TextView bluetoothDeviceTextview = (TextView) listItemView.findViewById(R.id.btdevice_name);
       bluetoothDeviceTextview.setText(currentBTDevice.getName());

        ImageView iconView = (ImageView) listItemView.findViewById(R.id.btdevice_list_item_icon);
        /*
        if(state == 1)
            iconView.setImageResource(R.drawable.ic_action_connecting);
        else if(state == 0)
        {
            iconView.setImageResource(R.drawable.ic_action_connected);
        }*/
        return listItemView;
    }

}
