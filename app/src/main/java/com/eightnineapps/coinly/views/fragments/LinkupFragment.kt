package com.eightnineapps.coinly.views.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.fragmentviewmodels.LinkupFragmentViewModel


class LinkupFragment : Fragment() {

    private lateinit var linkupFragmentViewModel: LinkupFragmentViewModel

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        linkupFragmentViewModel = ViewModelProvider(this).get(LinkupFragmentViewModel::class.java)
        val view = inflater.inflate(R.layout.fragment_linkup, container, false)
        return createLinkupTab(view)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        val searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = true
        linkupFragmentViewModel.setUpSearchView(searchIcon.actionView as SearchView)
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
     * Sets up the linkup tab fragment for the user
     */
    private fun createLinkupTab(view: View): View {
        linkupFragmentViewModel.addAllUsersToRecyclerView(view.findViewById(R.id.allUsersRecyclerView), context)
        return view
    }
}