package com.eightnineapps.coinly.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.eightnineapps.coinly.R

/**
 * A Single fragment class. Represents a single tab in the tab layout on the home activity
 * Can represent the Big, Little, or Linkup tab
 */
class HomeFragment : Fragment() {

    private var currentTabPosition = 0
    private val BIGS_TAB = 0
    private val LITTLES_TAB = 1
    private val LINKUP_TAB = 2

    /**
     * Use the passed in position Int from the ViewPagerAdapter class to determine the correct
     * layout to inflate
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var fragmentLayoutId = -1

        currentTabPosition = arguments?.getInt("FRAGMENT_ID")!!

        when (currentTabPosition) {
            BIGS_TAB -> fragmentLayoutId = R.layout.fragment_home
            LITTLES_TAB -> fragmentLayoutId = R.layout.fragment_home
            LINKUP_TAB -> fragmentLayoutId = R.layout.fragment_home
        }

        val view = inflater.inflate(fragmentLayoutId, container, false) // Inflate the layout for this fragment
        val text = view.findViewById<TextView>(R.id.text_display)
        text.text = arguments?.getString("message")
        return view
    }

}
