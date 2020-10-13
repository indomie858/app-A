package com.example.appa.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Path;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.databinding.PlaceTileBinding;
import com.example.appa.db.PlaceEntity;
import com.example.appa.ui.navigation.DirectionsActivity;
import com.example.appa.ui.navigation.MapWithNavActivity;

import java.util.ArrayList;
import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>  {
    // For debugging
    public static final String TAG = "RecyclerviewAdapter";

    // Our list of places
    private List<PlaceEntity> mPlaces = new ArrayList<>();

    public void setPlaces(List<PlaceEntity> places) {
        this.mPlaces = places;
        notifyDataSetChanged();
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

    // this method is responsible
    // for inflating the layout for our views.
    @Override
    @NonNull
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
        PlaceTileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.place_tile, parent, false);
        return new PlaceViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull PlaceViewHolder holder, int position) {
        // This method is called any time
        // an item is added to the list.
        PlaceEntity currentPlace = mPlaces.get(position);
        holder.binding.setPlace(currentPlace);

        // Attach this listener to every button,
        // which will set the view model for the direction activity
        // then launch that activity.
        holder.binding.getRoot().findViewById(R.id.app_manual_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context viewContext = v.getContext();
                Toast.makeText(viewContext, holder.binding.getPlace().getName(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(viewContext, DirectionsActivity.class);
                intent.putExtra("NewPlace", currentPlace.getId());
                viewContext.startActivity(intent);
            }
        });
        holder.binding.executePendingBindings();
    }


    @Override
    public int getItemCount() {
       return mPlaces.size();
    }

}
