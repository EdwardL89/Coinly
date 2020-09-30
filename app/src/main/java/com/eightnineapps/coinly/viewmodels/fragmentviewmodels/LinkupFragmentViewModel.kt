package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.classes.helpers.SearchQueryHelper
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class LinkupFragmentViewModel: ViewModel() {

    private var hasLoadedUsers = false
    private var allUsersQueryTask: Task<QuerySnapshot>? = null
    private var recyclerAdapter: UsersRecyclerViewAdapter? = null
    private var allUsers = mutableListOf<Triple<String, String, String>>()
    private val searchQueryHelper = SearchQueryHelper()

    /**
     * Returns the recycler's adapter
     */
    fun getAdapter() = recyclerAdapter!!

    /**
     * Determines whether or not the the users have compiled to the allUsers list
     */
    fun hasLoadedUsers() = hasLoadedUsers

    /**
     * Returns the query task
     */
    fun getAllUsersQuery() = allUsersQueryTask

    /**
     * Instantiates the adapter for the recycler
     */
    fun createAdapter() {
        recyclerAdapter = UsersRecyclerViewAdapter(allUsers)
    }

    /**
     * Initiates the query for all the users
     */
    fun startQueryForAllUsers() {
        allUsersQueryTask = Firestore.getInstance().collection("users").get()
    }

    /**
     * Saves all users from the document query as Triples
     */
    fun compileUserDataToList(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            if (document["displayName"] != CurrentUser.displayName.toString()) {
                allUsers.add(Triple(document["profilePictureUri"].toString(),
                    document["displayName"].toString(),
                    document["email"].toString()))
            }
        }
        hasLoadedUsers = true
    }

    /**
     * Overrides the search query functions
     */
    fun setUpSearchView(searchView: SearchView) {
        searchQueryHelper.setUpSearchView(searchView)
    }

    /**
     * Provides the data to filter the list
     */
    fun setUpDataForSearchView() {
        searchQueryHelper.setOriginalList(allUsers)
        searchQueryHelper.setAdapter(recyclerAdapter!!)
    }
}