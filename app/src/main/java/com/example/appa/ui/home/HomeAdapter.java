package com.example.appa.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.appa.R;

public class HomeAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] numWord;
    private int[] numImages;

    public HomeAdapter(Context c, String[] numWord, int[] numImages){
        context =c;
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

        if (inflater==null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (view==null){
            view = inflater.inflate(R.layout.row_item,null);
        }

        ImageView imageView = view.findViewById(R.id.image_view);
        TextView textView = view.findViewById(R.id.text_view);

        imageView.setImageResource(numImages[position]);
        textView.setText(numWord[position]);

        return view;
    }



}
