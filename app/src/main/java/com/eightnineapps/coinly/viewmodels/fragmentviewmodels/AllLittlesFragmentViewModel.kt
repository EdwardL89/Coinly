package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.classes.helpers.SearchQueryHelper
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

class AllLittlesFragmentViewModel: ViewModel() {

    private var hasLoadedUsers = false
    private var allLittlesQueryTask: Task<QuerySnapshot>? = null
    private var recyclerAdapter: UsersRecyclerViewAdapter? = null
    private var allLittles = mutableListOf<Triple<String, String, String>>()
    private val searchQueryHelper = SearchQueryHelper()

    /**
     * Returns the recycler's adapter
     */
    fun getAdapter() = recyclerAdapter!!

    /**
     * Determines whether or not the the users have compiled to the allLittles list
     */
    fun hasLoadedUsers() = hasLoadedUsers

    /**
     * Returns the query task
     */
    fun getAllLittlesQuery() = allLittlesQueryTask

    /**
     * Instantiates the adapter for the recycler
     */
    fun createAdapter() {
        recyclerAdapter = UsersRecyclerViewAdapter(allLittles)
    }

    /**
     * Removes a user from the recycler view
     */
    fun removeUser(user: User) {
        allLittles.remove(allLittles.first { it.second == user.displayName })
    }

    /**
     * Adds a user from the recycler view
     */
    fun addUser(document: DocumentSnapshot) {
        allLittles.add(Triple(document["profilePictureUri"].toString(),
            document["displayName"].toString(),
            document["email"].toString()))
    }

    /**
     * Initiates the query for all the user's Littles
     */
    fun startQueryForAllLittles() {
        allLittlesQueryTask = Firestore.getLittles(CurrentUser.instance!!.email!!).get()
    }

    /**
     * Saves all users from the document query as Triples
     */
    fun compileUserDataToList(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allLittles.add(Triple(document["profilePictureUri"].toString(),
                document["displayName"].toString(),
                document["email"].toString()))
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
        searchQueryHelper.setOriginalList(allLittles)
        searchQueryHelper.setAdapter(recyclerAdapter!!)
    }
}