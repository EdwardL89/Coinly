package com.eightnineapps.coinly.viewmodels

import android.content.Context
import com.eightnineapps.coinly.classes.TabLayoutFragmentViewModel
import com.eightnineapps.coinly.models.CurrentUser

class AllBigsFragmentViewModel: TabLayoutFragmentViewModel() {

    fun addAllBigsToRecyclerView(context: Context?) {
        addUsersToRecyclerView(CurrentUser.instance!!.bigs, context)
    }

}