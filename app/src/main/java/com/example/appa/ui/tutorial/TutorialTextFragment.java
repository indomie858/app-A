package com.example.appa.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.appa.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialTextFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    FragmentTransaction fragmentTransaction;
    int position = 0;


    public TutorialTextFragment() {
        // Required empty public constructor

    }

    public static TutorialTextFragment newInstance(int position) {
        TutorialTextFragment variable1 = new TutorialTextFragment();
        Bundle variable2 = new Bundle();
        variable2.putInt("position", position);
        variable1.setArguments(variable2);
        return variable1;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //some variable
           int variable3 = getArguments().getInt("position", 0);
        // switch case
        switch(variable3) {
            case 0:
                return  inflater.inflate(R.layout.fragment_tutorial_text,container,false);
            case 1:
                return  inflater.inflate(R.layout.fragment_tutorial_text,container,false);
            case 2:
                return  inflater.inflate(R.layout.maneuver_row,container,false);
            case 3:
                return  inflater.inflate(R.layout.fragment_tutorial_text,container,false);
            case 4:
                return  inflater.inflate(R.layout.fragment_tutorial_text,container,false);
            case 5:
                return  inflater.inflate(R.layout.fragment_tutorial_text,container,false);
            default:
                return null;
        }

        // Inflate the layout for this fragment
        //  View view = inflater.inflate(R.layout.fragment_tutorial_text,container,false);



        // return view;
    }



}