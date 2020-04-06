package com.eightnineapps.coinly.fragments

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity
import com.eightnineapps.coinly.activities.LoginActivity
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.interfaces.CallBack
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class AllLittlesFragment : Fragment() {

    private var SHOW_SEARCH_ICON = true
    private var numOfLittleEmails = 1
    private var littleEmailsCounter = 1
    private lateinit var searchIcon: MenuItem
    private lateinit var currentLittlesEmails: MutableList<*>
    private lateinit var allLittlesRecyclerViewList: RecyclerView
    private var allLittlesToDisplay: MutableList<DocumentSnapshot> = ArrayList()

    companion object {
        var allLittles: MutableList<DocumentSnapshot> = ArrayList()
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
        searchIcon.isVisible = SHOW_SEARCH_ICON
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Determines what the item menus in the app bar option items do
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) { // Programs the search button for the recycler view
            val searchView = searchIcon.actionView as SearchView
            setUpSearchView(searchView, allLittles, allLittlesToDisplay, allLittlesRecyclerViewList)
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
     * Sets up the little tab fragment for the user
     */
    private fun createLittlesTab(view: View): View {
        showSearchIcon(true)
        allLittlesRecyclerViewList = view.findViewById(R.id.allLittlesRecyclerView)
        allLittles.clear()
        allLittlesToDisplay.clear()
        queryFirestoreForAllAssociates(false, object: CallBack {
            override fun secondQueryCallBack(userEmails: MutableList<*>) {
                littleEmailsCounter = 1
                numOfLittleEmails = userEmails.size
                for (email in userEmails) queryFirestoreForSingleProfile(false, email)
            }
        })
        return view
    }

    /**
     * Queries the Firestore to retrieve all Bigs or Littles (associates) the current user has
     */
    private fun queryFirestoreForAllAssociates(queryForBigs: Boolean, callback: CallBack) {
        HomeActivity.database.collection("users").document(LoginActivity.auth.currentUser?.email!!).get().addOnCompleteListener {
                task -> addAssociatesToList(queryForBigs, task, callback)
        }
    }

    /**
     * Queries the firestore for a user profile by email and adds them to the respective list
     * in the involved helper methods
     */
    private fun queryFirestoreForSingleProfile(queryForBigs: Boolean, email: Any?) {
        HomeActivity.database.collection("users").document(email.toString()).get().addOnCompleteListener {
                subtask -> addObservedUserToList(queryForBigs, subtask)
        }
    }

    /**
     * Upon successful completion of the second query, add the retrieved users to the list to be displayed
     * and update the recycler view once this has been done for all emails of the current user's associates.
     */
    private fun addObservedUserToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>) {
        if (task.isSuccessful) {
            val currentUser = task.result!!
            allLittles.add(currentUser)
            allLittlesToDisplay.add(currentUser)
            if (littleEmailsCounter == numOfLittleEmails) updateRecyclerViewAdapterAndLayoutManager(allLittlesRecyclerViewList, allLittlesToDisplay)
            else littleEmailsCounter += 1
        }
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateRecyclerViewAdapterAndLayoutManager(recyclerViewList: RecyclerView, listToDisplay: MutableList<DocumentSnapshot>) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = UsersRecyclerViewAdapter(listToDisplay, context!!)
    }

    /**
     * Begin the callback for the second query (upon successful completion of the first query) that
     * gets the users with the matching emails
     */
    private fun addAssociatesToList(queryForBigs: Boolean, task: Task<DocumentSnapshot>, callBack: CallBack) {
        try {
            if (task.isSuccessful) {
                currentLittlesEmails = task.result!!.data?.get("littles") as MutableList<*>
                callBack.secondQueryCallBack(currentLittlesEmails)
            }
        } catch (e: Exception) {
            Log.w(LoginActivity.TAG, e.message.toString())
        }
    }

    /**
     * Toggle the search bar icon
     */
    private fun showSearchIcon(show: Boolean) {
        SHOW_SEARCH_ICON = show
        activity?.invalidateOptionsMenu()
    }
}