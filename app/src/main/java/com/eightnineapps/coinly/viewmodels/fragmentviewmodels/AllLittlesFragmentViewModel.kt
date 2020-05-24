package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.models.CurrentUser

class AllLittlesFragmentViewModel: TabLayoutFragmentViewModel() {

    private var hasLoadedData = false

    fun addAllLittlesToRecyclerView(recyclerView: RecyclerView, context: Context?) {
        setupRecycler(recyclerView)
        addSpaceBetweenItems(context)
        if (!hasLoadedData) {
            addUsersToRecyclerView(CurrentUser.instance!!.littles, context)
            hasLoadedData = true
        } else {
            updateRecyclerViewAdapterAndLayoutManager(context)
        }
    }

}