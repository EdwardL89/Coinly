package com.eightnineapps.coinly.viewmodels

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.classes.TabLayoutFragmentViewModel

class LinkupFragmentViewModel: TabLayoutFragmentViewModel() {

    private var hasLoadedData = false

    fun addAllUsersToRecyclerView(recyclerView: RecyclerView, context: Context?) {
        setupRecycler(recyclerView)
        addSpaceBetweenItems(context)
        if (!hasLoadedData) {
            getAllUsers(context)
            hasLoadedData = true
        } else {
            updateRecyclerViewAdapterAndLayoutManager(context)
        }
    }
}