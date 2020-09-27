package com.example.appa.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.example.appa.databinding.PlaceTileBinding;
import com.example.appa.viewmodel.*;


public class PlaceFragment extends Fragment {
    public PlaceTileBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = PlaceTileBinding.inflate(getLayoutInflater());
        return (View) binding.getPlace();
    }
/*
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        // Establishing the link between our UI and our ViewModel
        // This fragment will have access to
        // the nav list view model.
        super.onViewCreated(view, savedInstanceState);
        final PlaceViewModel placeViewModel = new ViewModelProvider(this).get(PlaceViewModel.class);
        binding.setPlace(placeViewModel)
    }
*/
}
