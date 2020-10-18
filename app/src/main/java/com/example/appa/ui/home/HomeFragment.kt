package com.example.appa.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.appa.R
import com.example.appa.ui.BluetoothConnectAcitvity
import com.example.appa.ui.MapActivity
import com.example.appa.ui.navigationlist.NavigationListActivity


class HomeFragment : Fragment(), View.OnClickListener {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)
        val connectButton: Button = view.findViewById(R.id.connect_button)
        val directoryButton: Button = view.findViewById(R.id.directory_button)
        val navigationButton: Button = view.findViewById(R.id.navigation_button)

        connectButton.setOnClickListener(this)
        directoryButton.setOnClickListener(this)
        navigationButton.setOnClickListener(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // you can add listener of elements here

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.navigation_button -> {
                val intent = Intent (getActivity(), MapActivity::class.java)
                getActivity()?.startActivity(intent)
            }

            R.id.directory_button -> {
                val intent = Intent (getActivity(), NavigationListActivity::class.java)
                getActivity()?.startActivity(intent)
            }

            R.id.connect_button -> {
                val intent = Intent (getActivity(), BluetoothConnectAcitvity::class.java)
                getActivity()?.startActivity(intent)
            }

            else -> {
            }
        }
    }


}
