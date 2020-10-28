package com.example.appa.bluetooth.threads;

import android.bluetooth.BluetoothSocket;

import com.example.appa.bluetooth.BluetoothHandler;
import com.example.appa.bluetooth.message.MessageHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class ConnectedDeviceThread extends Thread{

    private BluetoothHandler btHandler;
    private MessageHandler messageHandler;
    private BluetoothSocket btSocket;

    private InputStream btInStream;
    private OutputStream btOutStream;

    private boolean running = true;
    private boolean hasRead;

    public ConnectedDeviceThread(BluetoothHandler btHandler, MessageHandler messageHandler, BluetoothSocket btSocket) {
        this.btHandler = btHandler;
        this.btSocket = btSocket;
        this.messageHandler = messageHandler;

        btInStream = null;
        btOutStream = null;

        try {
            btInStream = btSocket.getInputStream();
            btOutStream = btSocket.getOutputStream();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void run() {
        setName("ConnectedDeviceThread");

        BufferedReader reader = new BufferedReader(new InputStreamReader(btInStream));
        String readLine;
        while(running) {
            try{
                readLine = reader.readLine();
                if(readLine != null)
                {
                    messageHandler.sendReadLine(readLine);
                }
            } catch (IOException e)
            {
                btHandler.setStatus(0);
                messageHandler.sendConnectionLost();
                break;
            }
        }
    }

    public void cancel()
    {
        try {
            btSocket.close();
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void writeToDevice(byte[] bytes)
    {
        try {
            btOutStream.write(bytes);
            messageHandler.sendBytes(bytes);
        } catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void readFromDevice() {
        try {
            InputStream btInStream = btSocket.getInputStream();
            byte[] buffer = new byte[256];
            int bytes;

            while (true)
            {
                try {
                    bytes = btInStream.read(buffer);
                    String readIn = new String(buffer, 0, bytes);
                    messageHandler.sendReadLine(readIn);
                } catch (IOException e) {
                    break;
                }
            }
        } catch (IOException e)
        {
            messageHandler.sendConnectionLost();
        }
    }

    public void shutdown() {
        running = false;

        if(btInStream != null)
        {
            try {
                btInStream.close();
            } catch (IOException e) {

            }
        }
    }
}
