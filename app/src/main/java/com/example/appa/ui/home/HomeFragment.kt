package com.example.appa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.GridView
import android.widget.LinearLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.appa.R
import com.example.appa.ui.navigationlist.NavigationListActivity

/*import com.example.appa.ui.navigationlist.NavigationListActivity*/
class HomeFragment : Fragment(), View.OnClickListener {

    var numWord = arrayOf("Classrooms", "Study", "Services", "Food & Drink", "Fitness", "Shopping")


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onClick(v: View?) {

    }


}
