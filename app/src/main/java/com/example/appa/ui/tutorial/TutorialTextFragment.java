package com.example.appa.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        TutorialTextFragment tutorialTextFragment = new TutorialTextFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        tutorialTextFragment.setArguments(bundle);
        return tutorialTextFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //some variable
           int variable3 = getArguments().getInt("position", 0);
        // switch case
        switch(variable3) {
            case 0:
                return  inflater.inflate(R.layout.tutorial_content_overview,container,false);
            case 1:
                return  inflater.inflate(R.layout.tutorial_content_step1,container,false);
            case 2:
                return  inflater.inflate(R.layout.tutorial_content_step2,container,false);
            case 3:
                return  inflater.inflate(R.layout.tutorial_content_step3,container,false);
            case 4:
                return  inflater.inflate(R.layout.tutorial_content_step4,container,false);
            case 5:
                return  inflater.inflate(R.layout.tutorial_content_step5,container,false);
            default:
                return null;
        }

    }

}