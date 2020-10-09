package com.example.appa.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appa.R;
import com.example.appa.ui.tutorial.TutorialActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
    BroadcastReceiver broadcastReceiver;
    ArrayAdapter<String> btArrayAdapter;
    ArrayAdapter<BluetoothDevice> btDeviceArray;
    BluetoothDevice btDevice;

    private static final int REQUEST_ENABLE_BT = 0;

    BluetoothSocket tmpSocket = null;
    BluetoothSocket mmSocket = null;

    ListView btListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bluetooth Setup
        bluetoothSetupPopup();

        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) ->{
            switch (item.getItemId()){
                case R.id.settings_button:
                    Intent settingsActivity = new Intent(MainActivity.this, SettingsActivity.class);
                    startActivity(settingsActivity);
                    break;
                case R.id.tutorial_button:
                    Intent tutorialActivity = new Intent(MainActivity.this, TutorialActivity.class);
                    startActivity(tutorialActivity);
                    break;
                case R.id.assistant_button:
            }
            return false;
        });
    }


    //Starts Map Activity using intent
    public void openMapActivity(View view) {
        Intent mapActivity = new Intent(MainActivity.this, MapActivity.class);
        startActivity(mapActivity);
    }

    //Starts Navigation list activity using intent
    public void openNavigationListActivity(View view) {
        Intent navigationListActivity = new Intent(MainActivity.this, NavigationListActivity.class);
        startActivity(navigationListActivity);
    }

    public void appaButtonPressed(View view)
    {
        bluetoothSetupPopup();
    }
    //Starts Bluetooth Connect activity using intent
    public void bluetoothSetupPopup() {

        if(btAdapter != null)
        {
            if(!btAdapter.isEnabled())
            {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, filter);

            final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
            final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
            final View Viewlayout = inflater.inflate(R.layout.bluetooth_connect_dialog, findViewById(R.id.bt_connect_dialog));
            String[] deviceNames;
            popDialog.setTitle("Pair Cane");
            popDialog.setView(Viewlayout);

            btListView = Viewlayout.findViewById(R.id.BTListOfDevices);
            btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

            for(BluetoothDevice btDevice: btAdapter.getBondedDevices() )
            {
                //btDeviceArray.add(btDevice);
                btArrayAdapter.add(btDevice.getName() + "\n" + btDevice.getAddress());
            }

            popDialog.setAdapter(btArrayAdapter, new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int which)
                {
                    /*try{
                        btDevice = btDeviceArray.getItem(which);

                        Method m = btDevice.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class);
                    }
                    catch (IOException e)
                    {

                    }*/
                }
            });
            /*
            popDialog.setPositiveButton("Pair",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                        }
                    });*/


            popDialog.create();
            popDialog.show();

        }
        else
        {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported on this device", Toast.LENGTH_LONG);
        }
    }
}