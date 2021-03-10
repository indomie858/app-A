package com.example.appa.ui.navigationlist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
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
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NavigationListActivity extends AppCompatActivity {
    public static final String TAG = "NavigationListFragment";
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private PlaceAdapter placeAdapter;
    private NavigationListViewModel viewModel;
    private String queryName = "";
    private String queryCategory = "";
    private Context context;

    // Locationrequest is a data object that contains options for fusedlocationclient
    LocationRequest mLocationRequest;
    // LocationCallback is used for receiving API notifications
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        setContentView(R.layout.nav_list_activity);

        // Adapter for the RecyclerView UI
        placeAdapter = new PlaceAdapter(context);

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

        // Instantiate location client to get user's current location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Use getLastLocation to populate location data before the first location update.
        getLastLocation();

        // LocationRequest contains settings regarding location intervals
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(2000);

        // Instantiate location callback.
        // The location provider invokes LocationCallback.onLocationResult().
        // This is where we define the location update logic.
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
                placeAdapter.setLocations(currentLocation);
                placeAdapter.notifyDataSetChanged();
            }
        };
        // Begin location updates (on create and on resume)
        startLocationUpdates();
        // Update list from intent
        setViewModelFromIntent();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // Update the location on resume
        Activity currentActivity = this;
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(currentActivity, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            currentLocation = location;
                            placeAdapter.setLocations(currentLocation);
                            placeAdapter.notifyDataSetChanged();
                            
                        }
                    }
                });

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.getMainLooper());
    }
    private void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    // Stop location updates when the activity is no longer in view
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }


    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        // Update the location on resume
        startLocationUpdates();
        setViewModelFromIntent();
    }

    private void setViewModelFromIntent() {
        // Sets data from a given intent
        Intent intent  = getIntent();
        queryCategory = intent.getStringExtra("QueryCategory");
        UpdateRVAdapter();
    }


    @SuppressLint("MissingPermission")
    private void UpdateRVAdapter() {
        // Gets the list of places from the database and
        // send it to the adapter,
        // which is responsible for creating the layout
        // with the data.
        viewModel.searchQuery(queryName, queryCategory).observe(this, new Observer<List<PlaceEntity>>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onChanged(List<PlaceEntity> placeEntities) {
                // Create viewmodels from the placentities,
                // sort them,
                // pass the sorted items into the adapters
                List<PlaceViewModel> placeViewModels = new ArrayList<PlaceViewModel>();

                for (PlaceEntity placeEntity : placeEntities) {
                    PlaceViewModel placeViewModel = new PlaceViewModel(placeEntity, context);
                    placeViewModel.setLocationAndDistance(currentLocation);
                    placeViewModels.add(placeViewModel);
                }

                // Sort the viewmodels by increasing distance
                if (currentLocation != null) {
                    Collections.sort(placeViewModels, new Comparator<PlaceViewModel>() {
                        @Override
                        public int compare(PlaceViewModel o1, PlaceViewModel o2) {
                            if (o1.getDistance() < o2.getDistance()) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }
                    });
                }
                placeAdapter.setPlaces(placeViewModels);
                placeAdapter.notifyDataSetChanged();
            }
        });
    }
}
