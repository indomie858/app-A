package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;

<<<<<<< HEAD:app/src/main/java/com/example/appa/MainActivity.java
import android.view.View;
=======
import com.example.appa.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
>>>>>>> Issue23-Locations_SQLite_Database:app/src/main/java/com/example/appa/ui/MainActivity.java


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
}