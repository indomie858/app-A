package com.example.appa.ui.home;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appa.R;
import com.example.appa.ui.navigationlist.NavigationListActivity;

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] numWord;
    private int[] numImages;

    public HomeAdapter(Context c, String[] numWord, int[] numImages){
        context = c;
        this.numWord = numWord;
        this.numImages = numImages;
    }
    @Override
    public int getCount() {
        return numWord.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        if (inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view == null){
            view = inflater.inflate(R.layout.row_item,null);

            // Sets the onclick listener
            // for the individual grid items
            // that launches the directory
            // and passes it the selected category name
            view.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, NavigationListActivity.class);
                            intent.putExtra("QueryCategory", numWord[position]);
                            context.startActivity(intent);
                        }
                    }
            );
        }

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView textView = view.findViewById(R.id.text_view);
        imageView.setImageResource(numImages[position]);
        textView.setText(numWord[position]);

        return view;
    }



}
