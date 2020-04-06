package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.firebase.firestore.DocumentSnapshot
import kotlin.collections.ArrayList

class AllLittlesFragment : Fragment() {

    private lateinit var searchIcon: MenuItem

    companion object {
        var allLittles: MutableList<DocumentSnapshot> = ArrayList()
        var numOfLittleEmails = 1
        var littleEmailsCounter = 1
        lateinit var currentLittlesEmails: MutableList<*>
        lateinit var allLittlesRecyclerViewList: RecyclerView
        var allLittlesToDisplay: MutableList<DocumentSnapshot> = ArrayList()
    }

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_littles, container, false)
        return createLittlesTab(view)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = true
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Determines what the item menus in the app bar option items do
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) { // Programs the search button for the recycler view
            val searchView = searchIcon.actionView as SearchView
            (activity as HomeActivity).setUpSearchView(searchView, allLittles, allLittlesToDisplay, allLittlesRecyclerViewList)
        }
        return super.onOptionsItemSelected(item)
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
        allLittlesRecyclerViewList = view.findViewById(R.id.allLittlesRecyclerView)
        allLittles.clear()
        allLittlesToDisplay.clear()
        (activity as HomeActivity).queryFirestoreForAllAssociates(false, object: CallBack {
            override fun secondQueryCallBack(userEmails: MutableList<*>) {
                littleEmailsCounter = 1
                numOfLittleEmails = userEmails.size
                for (email in userEmails) (activity as HomeActivity).queryFirestoreForSingleProfile(false, email, context)
            }
        })
        return view
    }
}