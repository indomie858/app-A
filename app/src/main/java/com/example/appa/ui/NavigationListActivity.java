package com.example.appa.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.viewmodel.NavigationListViewModel;

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

    }



}
