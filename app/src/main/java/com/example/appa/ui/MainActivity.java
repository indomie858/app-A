package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;


import android.view.View;

import com.example.appa.R;



import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void openMapActivity(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }
    public void openNavigationListAcitivity(View view) {
        Intent intent = new Intent(MainActivity.this, NavigationListActivity.class);
        startActivity(intent);
    }
}