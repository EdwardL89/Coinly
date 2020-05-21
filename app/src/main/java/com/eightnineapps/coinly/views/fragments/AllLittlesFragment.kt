package com.eightnineapps.coinly.views.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.AllLittlesFragmentViewModel

class AllLittlesFragment : Fragment() {

    private lateinit var allLittlesFragmentViewModel: AllLittlesFragmentViewModel

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        allLittlesFragmentViewModel = ViewModelProvider(this).get(AllLittlesFragmentViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_littles, container, false)
        return createLittlesTab(view)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        val searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = true
        allLittlesFragmentViewModel.setUpSearchView(searchIcon.actionView as SearchView)
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Sets up the little tab fragment for the user
     */
    private fun createLittlesTab(view: View): View {
        allLittlesFragmentViewModel.addAllLittlesToRecyclerView(view.findViewById(R.id.allLittlesRecyclerView), context)
        return view
    }
}