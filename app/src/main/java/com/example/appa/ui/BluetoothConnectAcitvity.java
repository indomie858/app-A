package com.example.appa.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appa.R;

public class BluetoothConnectAcitvity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    /*
    private static final int REQUEST_ENABLE_BT = 0;

    TextView mStatusBT, mPairedBT;
    Button mEnabledBT;

    BluetoothAdapter btAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_connect_acitvity);

        mStatusBT =  findViewById(R.id.statusBT);
        mPairedBT = findViewById(R.id.pairedBT);
        mEnabledBT = findViewById(R.id.enableBT);

        //adapter
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        //check if bt is available
        if(btAdapter == null)
        {
            mStatusBT.setText("Bluetooth is not available on this device");
        }
        else
        {
            mStatusBT.setText("Bluetooth is available on this device");
        }

        //prompt user to enable bluetooth
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if(!btAdapter.isEnabled())
        {
            mStatusBT.setText("Please enable Bluetooth");
        }
        else
        {
            mStatusBT.setText("Bluetooth is enabled");
        }
        //offer opportunity to enable bt through button
        mEnabledBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!btAdapter.isEnabled()) {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                }
                if(!btAdapter.isEnabled())
                {
                    mStatusBT.setText("Please enable Bluetooth");
                }
                else
                {
                    mStatusBT.setText("Bluetooth is enabled");
                }
            }
        });*/
}