package com.example.appa.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.db.PlaceEntity;
import com.example.appa.ui.tutorial.TutorialActivity;
import com.example.appa.viewmodel.NavigationListViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

public class NavigationListActivity extends AppCompatActivity {
    public static final String TAG = "NavigationListFragment";
    private PlaceAdapter placeAdapter;
    private NavigationListViewModel viewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_list_activity);

        //This handles the back navigation button on top app bar
        MaterialToolbar actionbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        if (null != actionbar) {
            actionbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);

            actionbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(NavigationListActivity.this);
                }
            });

            // Inflate a menu to be displayed in the toolbar
            //actionbar.inflateMenu(R.menu.settings);
        }
        //end of top app bar code


        placeAdapter = new PlaceAdapter();
        viewModel = new ViewModelProvider(this).get(NavigationListViewModel.class);

        // Update the recycler view adapter with a list of place entities.
        viewModel.getAllPlaces().observe(this, new Observer<List<PlaceEntity>>() {
            @Override
            public void onChanged(List<PlaceEntity> placeEntities) {
                placeAdapter.setPlaces(placeEntities);
            }
        });


        // Adapter will load in places.
        // For now it just gives a list of generic places.
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
                    break;
                case R.id.tutorial_button:
                    Intent tutorialActivity = new Intent(NavigationListActivity.this, TutorialActivity.class);
            }
            return false;
        });

    }



}
