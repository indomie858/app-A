package com.example.appa.ui.mapbox;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;

import java.util.Arrays;
import java.util.List;


/**
 * This is an adapter for the recyclerview used in DirectionsActivity. This uses the layout file
 * called destination_row.xml and maneuver_row.xml
 */
public class DirectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    //private ItemClickListener mClickListener;


    // data is passed into the constructor
    DirectionsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mData = data;
    }

    //set navigation data from directionsactivity
    public void setData(List<String> data) {
        this.mData = data;
    }

    //viewholder for destinationview
    class DestinationViewHolder extends RecyclerView.ViewHolder {
        TextView destinationTextView;
        TextView distanceRemainingTextView;
        TextView upcomingInstructionTextView;
        TextView compassHeading;

        DestinationViewHolder(View itemView) {
            super(itemView);
            destinationTextView = itemView.findViewById(R.id.destination);
            distanceRemainingTextView = itemView.findViewById(R.id.distanceRemaining);
            upcomingInstructionTextView = itemView.findViewById(R.id.upcomingInstruction);
        }
    }

    //viewholder for maneverview
    class ManeuverViewHolder extends RecyclerView.ViewHolder {
        TextView maneuverTextView;

        ManeuverViewHolder(View itemView) {
            super(itemView);
            maneuverTextView = itemView.findViewById(R.id.manueverInstruction);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Defines what the view should be at item position x.
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //destination row
        if (viewType == 0) {
            View destinationView = mInflater.inflate(R.layout.destination_row, parent, false);
            return new DestinationViewHolder(destinationView);
            //everything else lol
        }  else {
            View maneuverView = mInflater.inflate(R.layout.maneuver_row, parent, false);
            return new ManeuverViewHolder(maneuverView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //destination row
        if (holder.getItemViewType() == 0) {
            DestinationViewHolder destinationViewHolder = (DestinationViewHolder) holder;
            String outputText = mData.get(position);
            //NOTE: structure of outputText is "$destinationName,$distanceRemaining,$distanceToNextStep,$upcomingInstruction"
            List<String> navigationInfo = Arrays.asList(outputText.split(","));
            destinationViewHolder.destinationTextView.setText(navigationInfo.get(0));
            destinationViewHolder.destinationTextView.setContentDescription("\u00A0");
            destinationViewHolder.upcomingInstructionTextView.setContentDescription("\u00A0");

            //this checks if distance setting is set to imperial or metric
            if (getDistanceUnitSetting().equals("mi")){
                destinationViewHolder.distanceRemainingTextView.setText(navigationInfo.get(1) + " feet remaining");
                destinationViewHolder.upcomingInstructionTextView.setText(navigationInfo.get(2) + " In " + navigationInfo.get(3) + " feet, " + navigationInfo.get(4));
            } else {
                destinationViewHolder.distanceRemainingTextView.setText(navigationInfo.get(1) + " meters remaining");
                destinationViewHolder.upcomingInstructionTextView.setText(navigationInfo.get(2) + " In " + navigationInfo.get(3) + " meters, " + navigationInfo.get(4));
            }
        }
        else { //everything else lol
            ManeuverViewHolder maneuverViewHolder = (ManeuverViewHolder) holder;
            String maneuver = mData.get(position);
            maneuverViewHolder.maneuverTextView.setText(maneuver);
            //maneuverViewHolder.maneuverTextView.setContentDescription("\u00A0");
        }
    }

    private String getDistanceUnitSetting(){
        // Set distance text with unit
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        String dunit = prefs.getString("dunit","");
        return dunit;
    }
}
