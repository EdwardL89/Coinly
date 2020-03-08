package com.eightnineapps.coinly.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.eightnineapps.coinly.R

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_home, container, false) // Inflate the layout for this fragment
        val text = view.findViewById<TextView>(R.id.text_display)
        text.text = arguments?.getString("message")
        return view
    }

}
