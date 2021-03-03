package com.example.appa.ui.tutorial;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.appa.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialTextFragment2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialTextFragment2 extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public TutorialTextFragment2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TutorialTextFragment.
     */
    public static TutorialTextFragment2 newInstance() {
        return new TutorialTextFragment2();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.cane_manual, container, false);
    }
}