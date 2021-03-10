package com.example.appa.ui.navigationlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.appa.R;
import com.example.appa.db.PlaceEntity;
import com.example.appa.viewmodel.MapWithNavViewModel;

public class DirectoryInformationActivity extends AppCompatActivity {

    private MapWithNavViewModel viewModel = null;
    private PlaceEntity currentPlace = null;
    private int currentPlaceID;

    private TextView nameText;
    private TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_information);

        nameText = findViewById(R.id.nameTextView);
        descriptionText = findViewById(R.id.descriptionTextView);

        viewModel = new ViewModelProvider(this).get(MapWithNavViewModel.class);

        setPlaceFromIntent();

        Button button= (Button)findViewById(R.id.testButton);
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (currentPlace != null) {
                    nameText.setText(currentPlace.getName());
                    descriptionText.setText(currentPlace.getDescription());
                }
            }
        });

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                populateViewsWithPlaceData();
            }
        }, 1000);
    }

    private void populateViewsWithPlaceData() {
        if (currentPlace != null) {
            nameText.setText(currentPlace.getName());
            descriptionText.setText(currentPlace.getDescription());
        }
    }

    private void setPlaceFromIntent() {
        Intent intent = getIntent();
        currentPlaceID = intent.getIntExtra("NewPlace", 1);
        Observer<PlaceEntity> placeEntityObserver = new Observer<PlaceEntity>() {
            @Override
            public void onChanged(PlaceEntity placeEntity) {
                currentPlace = placeEntity;
            }
        };
        viewModel.getPlaceFromID(currentPlaceID).observeForever(placeEntityObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}