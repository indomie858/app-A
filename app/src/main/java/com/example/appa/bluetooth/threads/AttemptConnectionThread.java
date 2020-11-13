package com.example.appa.bluetooth.threads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import com.example.appa.bluetooth.BluetoothHandler;
import com.example.appa.bluetooth.message.MessageHandler;

import java.io.IOException;
import java.util.UUID;

public class AttemptConnectionThread extends Thread{

    private BluetoothHandler btHandler;
    private MessageHandler messageHandler;
    private BluetoothSocket btSocket;
    private BluetoothDevice btDevice;

    public AttemptConnectionThread(BluetoothHandler btHandler, MessageHandler messageHandler, BluetoothDevice btDevice) {
        this.btHandler = btHandler;
        this.messageHandler = messageHandler;
        this.btDevice = btDevice;
        BluetoothSocket tempSocket = null;
        ParcelUuid list[] = btDevice.getUuids();
        UUID btDeviceUuid;
        btDeviceUuid = UUID.fromString(list[0].toString());


        try {
            //UUID uuid = UUID.randomUUID();
            //Method method = btDevice.getClass().getMethod("createRfCommSocket", int.class);
            //tempSocket = (BluetoothSocket) method.invoke(btDevice, params);

            tempSocket = btDevice.createRfcommSocketToServiceRecord(btDeviceUuid);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
        btSocket = tempSocket;
    }

    public void run() {
        setName("AttemptConnectionThread");
        try{
            //popup here that indicates device is connecting

            btSocket.connect();

            if(btSocket.isConnected())
            {
                Log.e("Connect--------", "No, it is not connected, try again :'(");
            }
            else {
                Log.e("Connect--------", "Nawice <o/");
            }
        } catch(IOException e)
        {
            System.out.println(e.getMessage());
            btHandler.setStatus(0);
            messageHandler.sendConnectionFailed();
            try{
                btSocket.close();
            } catch (IOException e2)
            {
                System.out.println(e.getMessage());
            }
        }

        synchronized (btHandler)
        {
            btHandler.setAttemptConnectionThread(null);
        }

        btHandler.connected(btDevice, btSocket);
    }

    public void cancel() {
        try {
            btSocket.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
