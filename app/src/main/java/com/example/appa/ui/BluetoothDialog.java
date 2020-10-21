package com.example.appa.ui;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import com.example.appa.R;
import com.example.appa.bluetooth.BluetoothHandler;
import com.example.appa.bluetooth.message.MessageHandler;

import java.util.List;

public class BluetoothDialog extends Activity {

    private final int REQUEST_ENABLE_BT = 2;

    private int status = 0;

    BluetoothHandler btHandler;
    String mostRecentRead;

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
                    break;
                case 2: //not connected
                    break;
                case 3: //connection failed
                    break;
                case 4: //connection lost
                    break;
                case 5: //connecting to device
                    break;
                case 6: //connection to device successful
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + message.what);
            }
        }
    };

    public interface BluetoothDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    BluetoothDialogListener listener;
    ArrayAdapter<BluetoothDevice> btArrayAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        btHandler = new BluetoothHandler(null, null);
        if(!btHandler.btAdapterEnabled()) {
            status = 2;
            Intent enableBt = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBt, REQUEST_ENABLE_BT);
        }

        while (status == 2); // loop until user returns from request

        setContentView(R.layout.bluetooth_connect_dialog);

        btArrayAdapter = new ArrayAdapter<BluetoothDevice>(this, android.R.layout.simple_list_item_1);

        for(BluetoothDevice btDevice : btHandler.getDevices())
        {
            btArrayAdapter.add(btDevice);
        }

        ListView listOfDevices = (ListView)findViewById(R.id.BTListOfDevices);
        listOfDevices.setAdapter(btArrayAdapter);

        listOfDevices.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btHandler.setMessageHandler(new MessageHandler(handler));
                btHandler.setAddress(btArrayAdapter.getItem(i).getAddress());
                btHandler.connect();
            }
        });

    }

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
    }

    public void onDestroy() {
        super.onDestroy();
        btHandler.disconnect();
    }
}

/*
    public Dialog onCreateDialog(Bundle savedInstanceState) {
*/

        //build dialog
        /*AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        Set<BluetoothDevice> pairedDevices = btHandler.getDevices();

        if(pairedDevices != null && !pairedDevices.isEmpty())
        {
            ArrayAdapter<BluetoothDevice> listOfDevices
        }*/

        /*if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(broadcastReceiver, filter);

            final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
            String[] deviceNames;
            popDialog.setTitle("Pair Cane");
            popDialog.setView(Viewlayout);

            btListView = Viewlayout.findViewById(R.id.BTListOfDevices);
            btArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

            for (BluetoothDevice btDevice : btAdapter.getBondedDevices()) {
                //btDeviceArray.add(btDevice);
                btArrayAdapter.add(btDevice.getName() + "\n" + btDevice.getAddress());
            }

            popDialog.setAdapter(btArrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    *//*try{
                        btDevice = btDeviceArray.getItem(which);

                        Method m = btDevice.getClass().getMethod("createRfcommSocketToServiceRecord", UUID.class);
                    }
                    catch (IOException e)
                    {

                    }*//*
                }
            });
            *//*
            popDialog.setPositiveButton("Pair",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {

                        }
                    });*//*


            popDialog.create();
            popDialog.show();

        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth not supported on this device", Toast.LENGTH_LONG);
        }

    }*/

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            listener = (BluetoothDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString());
        }
    }
*/

