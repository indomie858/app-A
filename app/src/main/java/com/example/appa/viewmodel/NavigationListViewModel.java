package com.example.appa.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.SavedStateHandle;

import com.example.appa.db.entity.PlaceEntity;
import com.example.appa.model.Place;

public class NavigationListViewModel extends AndroidViewModel {

    private final Place[] places = {
      new PlaceEntity(),
      new PlaceEntity(),
      new PlaceEntity()
    };

    public NavigationListViewModel(Application application, SavedStateHandle savedStateHandle) {
        super(application);
    }

    public Place[] getPlaces() {
        return places;
    }
}
