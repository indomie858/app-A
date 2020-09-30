package com.example.appa.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.SavedStateHandle;

import com.example.appa.db.PlaceEntity;
import com.example.appa.model.Place;

import java.util.ArrayList;

public class NavigationListViewModel extends AndroidViewModel {

    private ArrayList<Place> mPlaces;


    public NavigationListViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
        mPlaces = new ArrayList<Place>();
        mPlaces.add(new PlaceEntity("Jacaranda Hall"));
        mPlaces.add(new PlaceEntity("Sequoia Hall"));
        mPlaces.add(new PlaceEntity("Oviatt Library"));
        mPlaces.add(new PlaceEntity("Cypress Hall"));
        mPlaces.add(new PlaceEntity("Freudian Sip"));
        mPlaces.add(new PlaceEntity("Lot B5"));
        mPlaces.add(new PlaceEntity("Lot B4"));
        mPlaces.add(new PlaceEntity("CSUN Campus Store"));
        mPlaces.add(new PlaceEntity("Soraya Nazarian Center for the Performing Arts"));
}

    public ArrayList<Place> getPlaces() {
        return mPlaces;
    }
}
