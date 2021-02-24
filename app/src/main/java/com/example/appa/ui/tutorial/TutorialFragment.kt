package com.example.appa.ui.tutorial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.appa.R
import com.google.android.material.tabs.TabLayout


class TutorialFragment : View.OnClickListener, Fragment() {

    private lateinit var tutorialPagerAdapter: TutorialPagerAdapter
    private lateinit var viewPager: ViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tutorialPagerAdapter = TutorialPagerAdapter(activity, childFragmentManager)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = tutorialPagerAdapter
        val tabs: TabLayout = view.findViewById<TabLayout>(R.id.tutorial_tabs)
        tabs.setupWithViewPager(viewPager)

    }

    override fun onClick(v: View?) {
        TODO("Not yet implemented")
    }
}