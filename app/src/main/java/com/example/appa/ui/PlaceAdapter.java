package com.example.appa.ui;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.databinding.PlaceTileBinding;
import com.example.appa.db.entity.PlaceEntity;
import com.example.appa.model.Place;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder> {
    private Place[] places;

    public void setPlaces(Place[] places) {
        this.places = places;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final PlaceTileBinding binding;
        public PlaceViewHolder(PlaceTileBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public PlaceAdapter() {
        super();
    }

    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        PlaceTileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.place_tile, parent, false);
        return new PlaceViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        holder.binding.setPlace(places[position]);
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
       return 0;
    }
}
