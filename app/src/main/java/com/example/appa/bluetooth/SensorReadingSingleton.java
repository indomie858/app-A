package com.example.appa.bluetooth;

import android.view.View;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.example.appa.BR;

public class SensorReadingSingleton extends BaseObservable {
    private Integer distance;
    private static SensorReadingSingleton instance = new SensorReadingSingleton();
    public static SensorReadingSingleton getInstance() {
        return instance;
    }

    public void setDistance(Integer newDistance) {
        distance = newDistance;
        notifyPropertyChanged(BR.distance);
    }

    @Bindable
    public Integer getDistance() {
        return distance;
    }
}
