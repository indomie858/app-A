package com.example.appa.ui;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appa.R;
import com.example.appa.model.Place;

public class PlaceHandlers extends AppCompatActivity {
    public void onClick(Place place) {
        setContentView(R.layout.activity_map);
    }
}
