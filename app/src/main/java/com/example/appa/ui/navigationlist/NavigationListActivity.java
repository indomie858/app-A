package com.example.appa.ui.navigationlist;

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
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

public class NavigationListActivity extends AppCompatActivity {
    public static final String TAG = "NavigationListFragment";
    private PlaceAdapter placeAdapter;
    private NavigationListViewModel viewModel;
    private String queryName = "";
    private String queryCategory = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_list_activity);

        // TODO: Move this to a fragment.
        // This handles the back navigation button on top app bar
        MaterialToolbar actionbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        if (null != actionbar) {
            actionbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
            actionbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(NavigationListActivity.this);
                }
            });
        }
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

                // Return false to perform the default action
                // of showing any suggestions if available

                UpdateRVAdapter();
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
        // This line gets the list of places from the database,
        // and sends it to the adapter,
        // which is responsible for creating the layout
        // with the data.
        viewModel.getAllPlaces().observe(this, new Observer<List<PlaceEntity>>() {
            @Override
            public void onChanged(List<PlaceEntity> placeEntities) {
                placeAdapter.setPlaces(placeEntities);
            }
        });
    }

    public void UpdateRVAdapter() {
        viewModel.searchQuery(queryName, queryCategory).observe(this, new Observer<List<PlaceEntity>>() {
            @Override
            public void onChanged(List<PlaceEntity> placeEntities) {
                placeAdapter.setPlaces(placeEntities);
            }
        });
    }
}
