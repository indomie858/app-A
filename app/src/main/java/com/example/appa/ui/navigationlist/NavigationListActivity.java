package com.example.appa.ui.navigationlist;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.db.PlaceEntity;
import com.example.appa.viewmodel.NavigationListViewModel;
import com.example.appa.viewmodel.PlaceViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NavigationListActivity extends AppCompatActivity {
    public static final String TAG = "NavigationListFragment";
    private PlaceAdapter placeAdapter;
    private NavigationListViewModel viewModel;
    private String queryName = "";
    private String queryCategory = "";
    private Context context;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.nav_list_activity);

        // Adapter for the RecyclerView UI
        placeAdapter = new PlaceAdapter();

        // Set the listener for the searchview,
        // to respond to text updates.
        SearchView searchView = findViewById(R.id.place_search);

        class NavigationQueryTextListener implements SearchView.OnQueryTextListener {
            public NavigationQueryTextListener() {
                super();
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                queryName = newText;
                UpdateRVAdapter();
                // Return false to perform the default action
                // of showing any suggestions if available

                return false;
            }
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
        }

        searchView.setOnQueryTextListener(new NavigationQueryTextListener());

        // Binds the adapter to the recyclerview.
        RecyclerView recyclerView = findViewById(R.id.place_list);
        recyclerView.setAdapter(placeAdapter);

        // Viewmodel. Handles all data interactions between the UI and DB.
        viewModel = new ViewModelProvider(this).get(NavigationListViewModel.class);


        // Update list from intent
        setViewModelFromIntent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setViewModelFromIntent();
    }

    private void setViewModelFromIntent() {
        // Sets data from a given intent
        Intent intent  = getIntent();
        queryCategory = intent.getStringExtra("QueryCategory");
        UpdateRVAdapter();
    }

    private void UpdateRVAdapter() {
        // Gets the list of places from the database and
        // send it to the adapter,
        // which is responsible for creating the layout
        // with the data.
        viewModel.searchQuery(queryName, queryCategory).observe(this, new Observer<List<PlaceEntity>>() {
            @Override
            public void onChanged(List<PlaceEntity> placeEntities) {
                // Create viewmodels from the placentities,
                // sort them,
                // pass the sorted items into the adapters
                List<PlaceViewModel> placeViewModels = new ArrayList<PlaceViewModel>();

                LocationManager manager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

                for (PlaceEntity placeEntity : placeEntities) {
                    PlaceViewModel placeViewModel = new PlaceViewModel(placeEntity);
                    placeViewModel.setLocationAndDistance(manager);
                    placeViewModels.add(placeViewModel);
                }

                // Sort the viewmodels by increasing distance
                Collections.sort(placeViewModels, new Comparator<PlaceViewModel>() {
                    @Override
                    public int compare(PlaceViewModel o1, PlaceViewModel o2) {
                        if(o1.getDistance() < o2.getDistance()) {
                            return -1;
                        } else {
                            return 1;
                        }
                    }
                });

                placeAdapter.setPlaces(placeViewModels);
                placeAdapter.notifyDataSetChanged();
            }
        });
    }
}
