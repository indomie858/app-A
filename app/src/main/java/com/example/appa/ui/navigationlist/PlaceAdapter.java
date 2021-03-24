package com.example.appa.ui.navigationlist;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneNumberUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;
import com.example.appa.databinding.PlaceTileBinding;
import com.example.appa.db.EntranceEntity;
import com.example.appa.ui.mapbox.DirectionsActivity;
import com.example.appa.viewmodel.PlaceViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.content.Context.ACCESSIBILITY_SERVICE;
import static android.content.Context.LOCATION_SERVICE;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceViewHolder>  {
    // For debugging
    public static final String TAG = "RecyclerviewAdapter";
    // Our list of places
    private List<PlaceViewModel> mPlaceViewModels = new ArrayList<>();
    private TextToSpeech mTTSObject;
    Context mContext;
    AccessibilityManager am;

    // Keeps track of whether locations have been set yet
    private MutableLiveData<Boolean> locationsSet;

    public MutableLiveData<Boolean> getLocationsSet() {
        // Return this livedata object for an observer
        return locationsSet;
    }

    public void setLocations(Location location) {
        for (PlaceViewModel placeViewModel: mPlaceViewModels) {
            placeViewModel.setLocationAndDistance(location);
            placeViewModel.setNearestEntrance(location);
        }
        // When location values are set, inform the activity
        // that they are set.
        locationsSet.setValue(true);
    }


    public void setPlaces(List<PlaceViewModel> places) {
        locationsSet.setValue(false);
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
    public PlaceAdapter(Context context) {
        locationsSet = new MutableLiveData<Boolean>();
        locationsSet.setValue(false);
        this.mContext = context;
    }

    // this method is responsible
    // for inflating the layout for our views.
    @Override
    @NonNull
    public PlaceAdapter.PlaceViewHolder onCreateViewHolder(ViewGroup parent,  int viewType) {
        PlaceTileBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.place_tile, parent, false);
        LocationManager mLocationManager = (LocationManager) parent.getContext().getSystemService(LOCATION_SERVICE);

        // Set a member variable using viewgroup context
        mTTSObject = new TextToSpeech(parent.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTSObject.setLanguage(Locale.US);
            }
        });

        am = (AccessibilityManager) parent.getContext().getSystemService(ACCESSIBILITY_SERVICE);

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
                Intent intent = new Intent(viewContext, DirectionsActivity.class);

                // Gather entrance information in intent to pass to the directions activity.
                intent.putExtra("NewPlace", currentPlaceViewModel.getId());
                intent.putExtra("destinationLongitude", currentPlaceViewModel.getNearestEntranceLongitude());
                intent.putExtra("destinationLatitude", currentPlaceViewModel.getNearestEntranceLatitude());
                intent.putExtra("destinationMinor", currentPlaceViewModel.getNearestEntranceMinor());
                intent.putExtra("destinationMajor", currentPlaceViewModel.getPlaceMajor());
                viewContext.startActivity(intent);
            }
        });

        // Set distance text with unit
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String dunit = prefs.getString("dunit","");
        TextView distanceText = holder.binding.getRoot().findViewById(R.id.place_distance);
        if (currentPlaceViewModel.getDistance() != null) {
            if(dunit.equals("mi")) {
                distanceText.setText(currentPlaceViewModel.getDistanceFeet() + " feet");
            }else{
                distanceText.setText(Math.ceil(currentPlaceViewModel.getDistance() * 10) /10 + " meter");
            }
        } else {
            distanceText.setText("");
        }

        // Initially set description to be hidden
        TextView descriptionText = holder.binding.getRoot().findViewById(R.id.description);
        descriptionText.setVisibility(View.GONE);

        // TODO: Think about setting up these buttons as fragments later on

        // Set accessibility description for about button
        Button aboutBtn = holder.binding.getRoot().findViewById(R.id.about_btn);
        aboutBtn.setContentDescription("Tell Me About " + currentPlaceViewModel.getName());
        aboutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Context viewContext = v.getContext();
                Intent intent = new Intent(viewContext, DirectoryInformationActivity.class);
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
