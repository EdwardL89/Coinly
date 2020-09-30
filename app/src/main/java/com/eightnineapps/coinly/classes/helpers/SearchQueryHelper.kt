package com.eightnineapps.coinly.classes.helpers

import androidx.appcompat.widget.SearchView
import com.eightnineapps.coinly.adapters.UsersRecyclerViewAdapter
import java.util.*

/**
 * Class that provides search capabilities on tablayout recycler views
 */
class SearchQueryHelper {

    private var originalList = mutableListOf<Triple<String, String, String>>()
    private var queryResultList = mutableListOf<Triple<String, String, String>>()
    private var recyclerAdapter: UsersRecyclerViewAdapter? = null

    fun setOriginalList(list: MutableList<Triple<String, String, String>>) {
        originalList = list
    }

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
                if (newText!!.isNotEmpty()) populateDisplayedListBasedOnSearchText(newText)
                else resetDisplayedList()
                recyclerAdapter?.notifyDataSetChanged()
                return true
            }
        })
    }

    /**
     * Adds to the displayed list the matching raw data items based on the search bar text
     */
    private fun populateDisplayedListBasedOnSearchText(newText: String) {
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
    private fun resetDisplayedList() {
        queryResultList.clear()
        queryResultList.addAll(originalList)
        recyclerAdapter!!.replaceUsers(queryResultList)
    }
}