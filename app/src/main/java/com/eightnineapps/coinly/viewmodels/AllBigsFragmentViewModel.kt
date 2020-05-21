package com.eightnineapps.coinly.viewmodels

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.classes.TabLayoutFragmentViewModel
import com.eightnineapps.coinly.models.CurrentUser

class AllBigsFragmentViewModel: TabLayoutFragmentViewModel() {

    private var hasLoadedData = false

    fun addAllBigsToRecyclerView(recyclerView: RecyclerView, context: Context?) {
        setupRecycler(recyclerView)
        addSpaceBetweenItems(context)
        if (!hasLoadedData) {
            addUsersToRecyclerView(CurrentUser.instance!!.bigs, context)
            hasLoadedData = true
        } else {
            updateRecyclerViewAdapterAndLayoutManager(context)
        }
    }

}