package com.eightnineapps.coinly.classes.helpers

import androidx.appcompat.widget.SearchView
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import java.util.*

/**
 * Class that provides search capabilities on tablayout recycler views
 */
class SearchQueryHelper {

    private var recyclerAdapter: UsersRecyclerViewAdapter? = null
    private var originalList = mutableListOf<Triple<String, String, String>>()
    private var queryResultList = mutableListOf<Triple<String, String, String>>()

    /**
     * Sets the original list of users to preform the query on
     */
    fun setOriginalList(list: MutableList<Triple<String, String, String>>) {
        originalList = list
    }

    /**
     * Sets the adapter to notify of data set changes
     */
    fun setAdapter(adapter: UsersRecyclerViewAdapter) {
        recyclerAdapter = adapter
    }

    /**
     * Sets up the search action bar item to filter the raw data list and place matching items
     * into a displayed list to show in the recycler view
     */
    fun setUpSearchView(searchView: SearchView) {
        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = true
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText!!.isNotEmpty()) filterOriginalList(newText)
                else resetQueryList()
                recyclerAdapter?.notifyDataSetChanged()
                return true
            }
        })
    }

    /**
     * Adds to the displayed list the matching raw data items based on the search bar text
     */
    private fun filterOriginalList(newText: String) {
        queryResultList.clear()
        val search = newText.toLowerCase(Locale.ROOT)
        originalList.forEach {
            if (it.second.toLowerCase(Locale.ROOT).contains(search)) queryResultList.add(it)
        }
        recyclerAdapter!!.replaceUsers(queryResultList)
    }

    /**
     * Clears the displayed list and adds back in all the original data
     */
    private fun resetQueryList() {
        queryResultList.clear()
        queryResultList.addAll(originalList)
        recyclerAdapter?.replaceUsers(queryResultList)
    }
}