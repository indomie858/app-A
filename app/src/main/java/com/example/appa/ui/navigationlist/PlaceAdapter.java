package com.example.appa.ui.navigationlist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.databinding.PlaceTileBinding;
import com.example.appa.db.PlaceEntity;
import com.example.appa.model.Place;
import com.example.appa.ui.navigation.InstructionViewActivity;
import com.example.appa.viewmodel.PlaceViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>  {
    // For debugging
    public static final String TAG = "RecyclerviewAdapter";
    // Our list of places
    private List<PlaceViewModel> mPlaceViewModels = new ArrayList<>();


    public void setPlaces(List<PlaceViewModel> places) {
        this.mPlaceViewModels.clear();
        this.mPlaceViewModels.addAll(places);
        // Recyclerview thing to let it know our data has changed
        notifyDataSetChanged();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final PlaceTileBinding binding;
        final LocationManager manager;
        public PlaceViewHolder(PlaceTileBinding binding, LocationManager manager) {
            super(binding.getRoot());
            this.binding = binding;
            this.manager = manager;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceAdapter() {
        super();
    }

    // this method is responsible
    // for inflating the layout for our views.
    @Override
    @NonNull
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
        PlaceTileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.place_tile, parent, false);
        LocationManager mLocationManager = (LocationManager) parent.getContext().getSystemService(LOCATION_SERVICE);
        return new PlaceViewHolder(binding, mLocationManager);
    }



    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        // This method is called any time
        // an item is added to the list.
        PlaceViewModel currentPlaceViewModel = mPlaceViewModels.get(position);
        holder.binding.setPlace(currentPlaceViewModel);
        // Attach this listener to every button,
        // which will set the view model for the direction activity
        // then launch that activity.
        holder.binding.getRoot().findViewById(R.id.place_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context viewContext = v.getContext();
                Intent intent = new Intent(viewContext, InstructionViewActivity.class);
                intent.putExtra("NewPlace", currentPlaceViewModel.getId());
                viewContext.startActivity(intent);
            }
        });

        holder.binding.executePendingBindings();
    }


    @Override
    public int getItemCount() {
        return mPlaceViewModels.size();
    }

}
