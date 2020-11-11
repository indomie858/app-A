package com.example.appa.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.example.appa.R;
import com.example.appa.bluetooth.BluetoothHandler;
import com.example.appa.bluetooth.BluetoothServiceHandler;
import com.example.appa.bluetooth.display.BluetoothDisplayAdapter;
import com.example.appa.bluetooth.message.MessageHandler;
import com.example.appa.bluetooth.threads.ConnectedDeviceThread;

import java.util.ArrayList;
import java.util.List;

public class BluetoothDialog extends DialogFragment {
    private static final String LOG_TAG = BluetoothDialog.class.getSimpleName();
    public static final String TITLE = "";
    private final int REQUEST_ENABLE_BT = 2;

    private int status = 0;

    //BluetoothHandler btHandler;
//    BluetoothServiceHandler btServiceHandler;
    String mostRecentRead;

    TextView readLineView;
    Button bt_led;
    Button bt_sound;

    FragmentActivity dContext;

    BluetoothDialogListener listener;
    //<BluetoothDevice> btArrayAdapter;
    BluetoothDisplayAdapter btArrayAdapter;
    ArrayList<BluetoothDevice> deviceStuff = new ArrayList<BluetoothDevice>();

    String randomAddress;
    int lastconnectedbtindex;

    public final Handler handler = new Handler() {
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
                    readLineView.setText(mostRecentRead);
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

    private boolean bound;

    /*private ServiceConnection bluetoothServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            BluetoothServiceHandler.LocalBinder binder = (BluetoothServiceHandler.LocalBinder) iBinder;
            btServiceHandler = binder.getService();
            boolean bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bound = false;
        }
    };*/


    public static BluetoothDialog newInstance(int title) {
        BluetoothDialog frag = new BluetoothDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);
        return frag;
    }

    public interface BluetoothDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dContext = getActivity();

        int arguments = getArguments().getInt("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(dContext);
        LayoutInflater inflater = dContext.getLayoutInflater();
        View view = inflater.inflate(R.layout.bluetooth_connect_dialog, null);

        /*if(btServiceHandler == null)
        {
            startService();
        }



        readLineView = (TextView) dContext.findViewById(R.id.incoming_bt_message);


/////////////////////////////////////////////////////////////////////////////////////////
        //btHandler = new BluetoothHandler(null, null);
        if(!btServiceHandler.getBTHandler().btAdapterEnabled()) {
            status = 2;
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, REQUEST_ENABLE_BT);
        }

        while (status == 2); // loop until user returns from request

        dContext.setContentView(R.layout.bluetooth_connect_dialog);
        readLineView = (TextView) dContext.findViewById(R.id.incoming_bt_message);

        //btArrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1);
        //for(BluetoothDevice btDevice : btHandler.getDevices())
        //{
        //    btArrayAdapter.add(btDevice);
        //    Log.e(LOG_TAG, btDevice.toString());
        //}

        for(BluetoothDevice btDevice : btServiceHandler.getBTHandler().getDevices())
        {
            deviceStuff.add(btDevice);
        }
        btArrayAdapter = new BluetoothDisplayAdapter(dContext, deviceStuff);
        randomAddress = deviceStuff.get(0).getAddress();
        ListView listOfDevices = (ListView)dContext.findViewById(R.id.BTListOfDevices);
        listOfDevices.setAdapter(btArrayAdapter);

        listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btServiceHandler.setMessageHandler(new MessageHandler(handler));
                btServiceHandler.connectTo(btArrayAdapter.getItem(i).getAddress());
                lastconnectedbtindex = i;
            }
        });

        bt_led = (Button) dContext.findViewById(R.id.bt_light_button);
        bt_led.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //byte[] messageToDevice = {'1'};
                //btServiceHandler.writeToDevice(messageToDevice);
                btServiceHandler.connectTo(randomAddress);
            }
        });

        bt_sound = (Button) dContext.findViewById(R.id.bt_sound_button);
        bt_sound.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] messageToDevice = {'2'};
                btServiceHandler.writeToDevice(messageToDevice);
            }
        });*/
        builder.setView(view);
        Dialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }
/*
    @Override
    public void onActivityResult(int request, int result, Intent data) {
        switch (request) {
            case 0: //Request to connect btDevice
                if (result == Activity.RESULT_OK) {
                    MessageHandler messageHandler = new MessageHandler(handler);
                }
                break;
            case 2: //request to enable bt
                status = 0;
                break;
        }
    }*/

    /*public void startService()
    {
        Intent intent = new Intent(getActivity(), BluetoothServiceHandler.class);
        getApplicationContext().bindService(intent, bluetoothServiceConnection, Context.BIND_AUTO_CREATE);
        Log.d(LOG_TAG, "Dialog bound to service");
    }*/
}

