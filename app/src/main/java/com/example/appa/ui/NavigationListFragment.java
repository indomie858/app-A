package com.example.appa.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.appa.R;
import com.example.appa.databinding.NavListFragmentBinding;
import com.example.appa.viewmodel.NavigationListViewModel;

public class NavigationListFragment extends Fragment {
    public static final String TAG = "NavigationListFragment";
    private PlaceAdapter placeAdapter;
    private NavListFragmentBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.nav_list_fragment, container, false);

        // Adapter will load in places.
        // For now it just gives a list of generic places.
        placeAdapter = new PlaceAdapter();
        binding.placeList.setAdapter(placeAdapter);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavigationListViewModel viewModel = new ViewModelProvider(this).get(NavigationListViewModel.class);
        placeAdapter.setPlaces(viewModel.getPlaces());
    }
}
