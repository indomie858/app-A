package com.example.appa.bluetooth;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

// Defines several constants used when transmitting messages between the
// service and the UI.
public class MessageConstants {
    public static final int MESSAGE_READ = 0;
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_TOAST = 2;
    public static final int MESSAGE_CONNECTED = 3;
    public static final int MESSAGE_DISCONNECTED = 4;
    public static final int MESSAGE_CONNECTING = 5;
    public static final int MESSAGE_LOST_CONNECTION = 6;
}
