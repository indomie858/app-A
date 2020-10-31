package com.example.appa.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.appa.R
import com.example.appa.ui.navigationlist.NavigationListActivity


class HomeFragment : Fragment(), View.OnClickListener {
    var gridView: GridView? = null

    var numWord = arrayOf("Classroom", "Study", "Services", "Food&Drinks", "Sports&Fitness", "Shopping")




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)


        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // you can add listener of elements here

    }

    override fun onClick(v: View?) {

    }


}
