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
}
