package com.eightnineapps.coinly.views.fragments.tablayout

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.fragmentviewmodels.LinkupFragmentViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_linkup.view.*

class LinkupFragment : Fragment() {

    private lateinit var linkupFragmentViewModel: LinkupFragmentViewModel

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        linkupFragmentViewModel = ViewModelProvider(this).get(LinkupFragmentViewModel::class.java)
        linkupFragmentViewModel.startQueryForAllUsers()
    }

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return createLinkupTab(inflater.inflate(R.layout.fragment_linkup, container, false))
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
     * Sets up the linkup tab fragment for the user
     */
    private fun createLinkupTab(view: View): View {
        addAllUsersToRecycler(view)
        addSpaceBetweenItemsInRecycler(context, view)
        return view
    }

    /**
     * Makes sure the query task is completed before continuing
     */
    private fun addAllUsersToRecycler(view: View) {
        if (linkupFragmentViewModel.hasLoadedUsers()) {
            attachAdapter(view)
        } else {
            val allUsersQueryTask = linkupFragmentViewModel.getAllUsersQuery()!!
            if (allUsersQueryTask.isComplete) {
                handleQueryTask(allUsersQueryTask, view)
            } else {
                allUsersQueryTask.addOnCompleteListener {
                    handleQueryTask(allUsersQueryTask, view)
                }
            }
        }
    }

    /**
     * Attaches the recyclerview's adapter from when it was scrolled off screen
     */
    private fun attachAdapter(view: View) {
        view.allUsersRecyclerView.adapter = linkupFragmentViewModel.getAdapter()
    }

    /**
     * Gathers all the Users and sets up the recyclerview to place them in
     */
    private fun handleQueryTask(allUsersQueryTask: Task<QuerySnapshot>, view: View) {
        linkupFragmentViewModel.compileUserDataToList(allUsersQueryTask.result!!)
        linkupFragmentViewModel.createAdapter()
        linkupFragmentViewModel.setUpDataForSearchView()
        attachAdapter(view)
    }

    /**
     * Adds space between recycler view list itemsF
     */
    private fun addSpaceBetweenItemsInRecycler(context: Context?, view: View) {
        val itemDecorator = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        itemDecorator.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.space_between_list_items)!!)
        view.allUsersRecyclerView.addItemDecoration(itemDecorator)
    }
}