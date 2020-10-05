package com.example.appa.ui.tutorial;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.appa.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TutorialTextFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TutorialTextFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public TutorialTextFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment TutorialTextFragment.
     */
    public static TutorialTextFragment newInstance() {
        return new TutorialTextFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial_text, container, false);
    }
}