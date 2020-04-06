package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.*
import kotlin.collections.ArrayList

class LinkupFragment : Fragment() {

    private var SHOW_SEARCH_ICON = true
    private lateinit var searchIcon: MenuItem
    private lateinit var allUsersRecyclerViewList: RecyclerView
    private var allUsersToDisplay: MutableList<DocumentSnapshot> = ArrayList()

    companion object {
        var allUsers: MutableList<DocumentSnapshot> = ArrayList()
    }

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_linkup, container, false)
        return createLinkupTab(view)
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        searchIcon = menu.findItem(R.id.menu_search)
        searchIcon.isVisible = SHOW_SEARCH_ICON
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Determines what the item menus in the app bar option items do
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) { // Programs the search button for the recycler view
            val searchView = searchIcon.actionView as SearchView
            setUpSearchView(searchView, allUsers, allUsersToDisplay, allUsersRecyclerViewList)
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
     * Sets up the search action bar item to filter the raw data list and place matching items
     * into a displayed list to show in the recycler view
     */
    private fun setUpSearchView(searchView: SearchView,
                                rawDataList: MutableList<DocumentSnapshot>,
                                displayedList: MutableList<DocumentSnapshot>,
                                recyclerViewList: RecyclerView) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) {
                    populateDisplayedListBasedOnSearchText(rawDataList, displayedList, newText)
                } else {
                    resetDisplayedList(rawDataList, displayedList)
                }
                recyclerViewList.adapter?.notifyDataSetChanged()
                return true
            }
        })
    }

    /**
     * Adds to the displayed list the matching raw data items based on the search bar text
     */
    private fun populateDisplayedListBasedOnSearchText(rawDataList: MutableList<DocumentSnapshot>,
                                                       displayedList: MutableList<DocumentSnapshot>,
                                                       newText: String) {
        displayedList.clear()
        val search = newText.toLowerCase(Locale.ROOT)
        rawDataList.forEach {
            if (it["displayName"].toString().toLowerCase(Locale.ROOT).contains(search)) {
                displayedList.add(it)
            }
        }
    }

    /**
     * Clears the displayed list and adds back in all the original data
     */
    private fun resetDisplayedList(rawDataList: MutableList<DocumentSnapshot>, displayedList: MutableList<DocumentSnapshot>) {
        displayedList.clear()
        displayedList.addAll(rawDataList)
    }

    /**
     * Sets up the linkup tab fragment for the user
     */
    private fun createLinkupTab(view: View): View {
        showSearchIcon(true)
        allUsersRecyclerViewList = view.findViewById(R.id.allUsersRecyclerView)
        allUsers.clear()
        allUsersToDisplay.clear()
        getAllUsers()
        return view
    }

    /**
     * Toggle the search bar icon
     */
    private fun showSearchIcon(show: Boolean) {
        SHOW_SEARCH_ICON = show
        activity?.invalidateOptionsMenu()
    }

    /**
     * Queries the Firestore for all users
     */
    private fun getAllUsers() {
        HomeActivity.database.collection("users").get().addOnCompleteListener{
                task -> addUsersToList(task)
        }
    }

    /**
     * Adds the retrieved users to the allUsers list upon successful task completion
     */
    private fun addUsersToList(task: Task<QuerySnapshot>) {
        if (task.isSuccessful) {
            for (users in task.result!!) {
                if (users.data["email"] != auth.currentUser!!.email) {
                    allUsers.add(users)
                }
            }
            allUsersToDisplay.addAll(allUsers)
            updateRecyclerViewAdapterAndLayoutManager(allUsersRecyclerViewList, allUsersToDisplay)
        }
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateRecyclerViewAdapterAndLayoutManager(recyclerViewList: RecyclerView, listToDisplay: MutableList<DocumentSnapshot>) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = UsersRecyclerViewAdapter(listToDisplay, context!!)
    }
}