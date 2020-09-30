package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class LinkupFragmentViewModel: ViewModel() {

    private var hasLoadedUsers = false
    private var allUsersQueryTask: Task<QuerySnapshot>? = null
    private var recyclerAdapter: UsersRecyclerViewAdapter? = null
    private var allUsers = mutableListOf<Triple<String, String, String>>()

    fun getAdapter() = recyclerAdapter!!

    fun hasLoadedUsers() = hasLoadedUsers

    fun getAllUsersQuery() = allUsersQueryTask

    fun createAdapter() {
        recyclerAdapter = UsersRecyclerViewAdapter(allUsers)
    }

    fun startQueryForAllUsers() {
        allUsersQueryTask = Firestore.getInstance().collection("users").get()
    }

    fun compileUserDataToList(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allUsers.add(Triple(document["profilePictureUri"].toString(),
                document["displayName"].toString(),
                document["email"].toString()))
        }
        hasLoadedUsers = true
    }

    fun setUpSearchView(searchView: SearchView) {
        return
    }
}