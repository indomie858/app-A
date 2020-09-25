package com.example.appa.viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.example.appa.model.Place;

public class PlaceViewModel extends ViewModel {
    private final Place place;
    public final String name = "Test";
    public PlaceViewModel(Place place) {
        super();
        this.place = place;
    }
}
