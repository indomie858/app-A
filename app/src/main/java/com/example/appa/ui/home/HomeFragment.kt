package com.example.appa.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import com.example.appa.R
import com.example.appa.ui.navigation.InstructionViewActivity
import com.example.appa.ui.navigationlist.NavigationListActivity

/*import com.example.appa.ui.navigationlist.NavigationListActivity*/
class HomeFragment : Fragment(), View.OnClickListener {
    var gridView: GridView? = null

    var numWord = arrayOf("Classrooms", "Study", "Services", "Food & Drink", "Fitness", "Shopping")

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    var numImages = intArrayOf(R.drawable.ic_action_classroom, R.drawable.ic_action_study, R.drawable.ic_action_service,
    R.drawable.ic_action_food, R.drawable.ic_action_fitness, R.drawable.ic_action_shop)

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
