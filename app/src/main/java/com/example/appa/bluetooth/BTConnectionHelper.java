package com.example.appa.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.util.Log;
import com.example.appa.bluetooth.message.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.Executor;

public class BTConnectionHelper {
    // This class serves as a helper/utility class
    // Which handles the details of querying and conecting to Bluetooth.
    final String BLUETOOTHTAG = "Bluetooth Connection";

    // This is the generic UUID for a Serial Port Profile connection
    // With the android device.
    final String APPAUUID = "00001101-0000-1000-8000-00805F9B34FB";
    final UUID mUUID = UUID.fromString(APPAUUID);

    private Handler handler;

    // Keep track of threads.
    private ConnectedThread connectedThread;
    private ConnectThread connectThread;

    // BluetoothServiceHandler mBTServiceHandler;
    // BluetoothHandler mBluetoothHandler;
    BluetoothAdapter mBTAdapter;

    Activity activity;
    public Context mContext; // Should be context from MainActivity
    boolean found = false; // Flagged true as soon as the first discoverable APP-A device is seen

    public BTConnectionHelper(Context context, Handler handler) {

        // Context receiver register
        // specifically for receiving bluetooth broadcast events.
        mContext = context;
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter);

        // Handler, passed in from Activity.
        // Associated with the thread.
        // Used to relay information to other parts of the app.
        this.handler = handler;

        // Get the device's bluetooth adapter.
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        // Enabled bluetooth if not enabled.
        if(!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // TODO:
            // Implement bluetooth dialog when bluetooth is not enabled.
        }
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
                        // CONNECT TO DEVICE HERE
                        connectThread = new ConnectThread(device);
                        connectThread.start();

                        // Stop discovery here as soon as we have the device.
                        found = true;
                        mBTAdapter.cancelDiscovery();
                    }
                }
            }
        }
    };

    public synchronized void appaConnect() {
        // Start discovery and connection.
        found = false;
        mBTAdapter.startDiscovery();
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mDevice;

        public ConnectThread(BluetoothDevice device) {
            mBTAdapter.cancelDiscovery();
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mDevice = device;
            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // mmUUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                Log.e(BLUETOOTHTAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            mBTAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(BLUETOOTHTAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            new ConnectedThread(mmSocket).start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(BLUETOOTHTAG, "Could not close the client socket", e);
            }
        }
    }


    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private final BufferedReader reader;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(BLUETOOTHTAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(BLUETOOTHTAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            reader = new BufferedReader(new InputStreamReader(mmInStream));
        }

        public void run() {


            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    String readLine = reader.readLine();
                    // Send the obtained line to the handler.
                    Message readMsg = handler.obtainMessage(MessageConstants.MESSAGE_TOAST, readLine);
                    readMsg.sendToTarget();
                } catch (IOException e) {
                    Log.d(BLUETOOTHTAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_WRITE, -1, -1, bytes);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                Log.e(BLUETOOTHTAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                handler.sendMessage(writeErrorMsg);
            }
        }
        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(BLUETOOTHTAG, "Could not close the connect socket", e);
            }
        }
    }
}
