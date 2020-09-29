package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.viewmodel.NavigationListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NavigationListActivity extends AppCompatActivity {
    public static final String TAG = "NavigationListFragment";
    private PlaceAdapter placeAdapter;
    private NavigationListViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_list_activity);
        viewModel = new ViewModelProvider(this).get(NavigationListViewModel.class);
        placeAdapter = new PlaceAdapter();
        // Adapter will load in places.
        // For now it just gives a list of generic places.
        placeAdapter.setPlaces(viewModel.getPlaces());
        RecyclerView recyclerView = findViewById(R.id.place_list);
        recyclerView.setAdapter(placeAdapter);

        // The switch case below is for adding the actions for when you click on the bottom menu -- create a case for the other buttons
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) ->{
            switch (item.getItemId()){
                case R.id.settings_button:
                    Intent settingsActivity = new Intent(NavigationListActivity.this, SettingsActivity.class);
                    startActivity(settingsActivity);
                    break;
                case R.id.home_button:
                    Intent mainActivity = new Intent(NavigationListActivity.this,MainActivity.class);
                    startActivity(mainActivity);
            }
            return false;
        });

    }



}
