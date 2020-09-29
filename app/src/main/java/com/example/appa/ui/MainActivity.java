package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appa.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //Starts Map Activity using intent
    public void openMapActivity(View view) {
        Intent intent = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intent);
    }

    //Starts Navigation list activity using intent
    public void openNavigationListActivity(View view) {
        Intent intent = new Intent(MainActivity.this, NavigationListActivity.class);
        startActivity(intent);
    }
}