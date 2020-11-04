package com.example.appa.bluetooth.message;

import android.os.Handler;

public class MessageHandler {

    Handler handler;

    private int status = 0;

    public MessageHandler(Handler handler) {
        this.handler = handler;
    }

    public void sendBytes(byte[] bytes)
    {
        handler.obtainMessage(0, -1, -1, bytes).sendToTarget();
    }

    public void sendReadLine(String readLine)
    {
        handler.obtainMessage(1, -1, -1, readLine).sendToTarget();
    }

    public void sendNotConnected() {
        handler.obtainMessage(2).sendToTarget();
    }

    public void sendConnectionFailed() {
        handler.obtainMessage(3).sendToTarget();
    }

    public void sendConnectionLost() {
        handler.obtainMessage(4).sendToTarget();
    }

    public void sendConnectingTo(String deviceName) {
        handler.obtainMessage(5, -1, -1, deviceName).sendToTarget();
    }

    public void sendConnectedTo(String deviceName)
    {
        handler.obtainMessage(6, -1, -1, deviceName).sendToTarget();
    }
}
