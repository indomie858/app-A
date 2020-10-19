package com.example.appa.ui.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.appa.R
import com.google.android.material.tabs.TabLayout


class TutorialFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_tutorial, container, false)

        // The adapter will get attached to a viewpager

        // The adapter will get attached to a viewpager
        val adapter = TutorialPagerAdapter(activity, activity?.supportFragmentManager)
        val viewPager: ViewPager = view.findViewById<ViewPager>(R.id.view_pager)
        viewPager.adapter = adapter
        val tabs: TabLayout = view.findViewById<TabLayout>(R.id.tutorial_tabs)
        tabs.setupWithViewPager(viewPager)

        return view
    }


}