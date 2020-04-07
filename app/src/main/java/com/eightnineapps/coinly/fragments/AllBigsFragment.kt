package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.firebase.firestore.DocumentSnapshot
import kotlin.collections.ArrayList

class AllBigsFragment : Fragment() {

    private lateinit var searchIcon: MenuItem

    companion object {
        var numOfBigEmails = 1
        var bigEmailsCounter = 1
        lateinit var currentBigsEmails: MutableList<*>
        lateinit var allBigsRecyclerViewList: RecyclerView
        var allBigs: MutableList<DocumentSnapshot> = ArrayList()
        var allBigsToDisplay: MutableList<DocumentSnapshot> = ArrayList()
    }

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_bigs, container, false)
        return createBigsTab(view)
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
            (activity as HomeActivity).setUpSearchView(searchView, allBigs, allBigsToDisplay, allBigsRecyclerViewList)
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
     * Sets up the big tab fragment for the user
     */
    private fun createBigsTab(view: View): View {
        allBigsRecyclerViewList = view.findViewById(R.id.allBigsRecyclerView)
        allBigsRecyclerViewList.removeAllViews()
        allBigs.clear()
        allBigsToDisplay.clear()
        (activity as HomeActivity).queryFirestoreForAllAssociates(true, object: CallBack {
            override fun secondQueryCallBack(userEmails: MutableList<*>) {
                bigEmailsCounter = 1
                numOfBigEmails = userEmails.size
                for (email in userEmails) (activity as HomeActivity).queryFirestoreForSingleProfile(true, email, context)
            }
        })
        return view
    }
}