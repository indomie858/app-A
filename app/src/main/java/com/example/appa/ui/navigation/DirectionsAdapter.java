package com.example.appa.ui.navigation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.appa.R;

import java.util.List;


/**
 * This is an adapter for the recyclerview used in DirectionsActivity. This uses the layout file
 * called directions_row.xml
 */
public class DirectionsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mData;
    private LayoutInflater mInflater;
    //private ItemClickListener mClickListener;


    // data is passed into the constructor
    DirectionsAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    class DestinationViewHolder extends RecyclerView.ViewHolder {
        TextView destinationTextView;

        DestinationViewHolder(View itemView) {
            super(itemView);
            destinationTextView = itemView.findViewById(R.id.destination);
        }
    }

    class ManeuverViewHolder extends RecyclerView.ViewHolder {
        TextView maneuverTextView;

        ManeuverViewHolder(View itemView) {
            super(itemView);
            maneuverTextView = itemView.findViewById(R.id.manueverInstruction);
        }
    }

    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return position;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View destinationView = mInflater.inflate(R.layout.destination_row, parent, false);
            return new DestinationViewHolder(destinationView);
        } else {
            View maneuverView = mInflater.inflate(R.layout.maneuver_row, parent, false);
            return new ManeuverViewHolder(maneuverView);
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            if (holder.getItemViewType() == 0) {
                DestinationViewHolder destinationViewHolder = (DestinationViewHolder) holder;
                String destination = mData.get(position);
                destinationViewHolder.destinationTextView.setText(destination);
            }
            else {
                ManeuverViewHolder maneuverViewHolder = (ManeuverViewHolder) holder;
                String maneuver = mData.get(position);
                maneuverViewHolder.maneuverTextView.setText(maneuver);
        }
    }


    /*// inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.directions_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String maneuver = mData.get(position);
        holder.maneuverTextView.setText(maneuver);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView maneuverTextView;

        ViewHolder(View itemView) {
            super(itemView);
            maneuverTextView = itemView.findViewById(R.id.manueverInstruction);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }*/
}
