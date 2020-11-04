package com.example.appa.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.appa.R
import com.example.appa.ui.navigation.InstructionViewActivity
import com.example.appa.ui.navigationlist.NavigationListActivity

/*import com.example.appa.ui.navigationlist.NavigationListActivity*/
class HomeFragment : Fragment(), View.OnClickListener {

    var numWord = arrayOf("Classrooms", "Study", "Services", "Food & Drink", "Fitness", "Shopping")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)

        gridView = view.findViewById(R.id.grid_view)

        // Set the gridView adapter
        val adapter = HomeAdapter(activity, numWord, numImages)
        gridView!!.adapter = adapter

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onClick(v: View?) {
    }
}
