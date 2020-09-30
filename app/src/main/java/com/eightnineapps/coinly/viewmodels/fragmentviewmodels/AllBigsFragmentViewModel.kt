package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class AllBigsFragmentViewModel: ViewModel() {

    private var hasLoadedUsers = false
    private var allBigsQueryTask: Task<QuerySnapshot>? = null
    private var linearLayoutManager: LinearLayoutManager? = null
    private var recyclerAdapter: UsersRecyclerViewAdapter? = null
    private var allBigs = mutableListOf<Triple<String, String, String>>()

    fun getAdapter() = recyclerAdapter!!

    fun getLayoutManager() = linearLayoutManager!!

    fun hasLoadedUsers() = hasLoadedUsers

    fun getAllBigsQuery() = allBigsQueryTask

    fun createAdapter() {
        recyclerAdapter = UsersRecyclerViewAdapter(allBigs)
    }

    fun setLayoutManager(llm: LinearLayoutManager) {
        linearLayoutManager = llm
    }

    fun startQueryForAllBigs() {
        allBigsQueryTask = Firestore.getBigs(CurrentUser.instance!!.email!!).get()
    }

    fun compileUserDataToList(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allBigs.add(Triple(document["profilePictureUri"].toString(),
                document["displayName"].toString(),
                document["email"].toString()))
        }
        hasLoadedUsers = true
    }

    fun setUpSearchView(searchView: SearchView) {
        return
    }
}