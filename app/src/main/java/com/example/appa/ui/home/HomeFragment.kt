package com.example.appa.ui.home
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.appa.R

/*import com.example.appa.ui.navigationlist.NavigationListActivity*/


class HomeFragment : Fragment(), View.OnClickListener {



    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view: View = inflater.inflate(R.layout.fragment_home, container, false)


        //gridView = view.findViewById(R.id.grid_view)

        //HomeAdapter adapter = new HomeAdapter(HomeFragment.this,numWord,numImages);
        //val adapter = HomeAdapter(activity, numWord, numImages)
        //gridView!!.adapter = adapter


        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // you can add listener of elements here

    }

    override fun onClick(v: View?) {

    }


}
