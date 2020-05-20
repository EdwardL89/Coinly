package com.eightnineapps.coinly.viewmodels

import android.content.Context
import com.eightnineapps.coinly.classes.TabLayoutFragmentViewModel
import com.eightnineapps.coinly.models.CurrentUser

class AllLittlesFragmentViewModel: TabLayoutFragmentViewModel() {

    fun addAllLittlesToRecyclerView(context: Context?) {
        addUsersToRecyclerView(CurrentUser.instance!!.littles, context)
    }

}