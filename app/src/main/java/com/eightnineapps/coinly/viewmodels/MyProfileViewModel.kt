package com.eightnineapps.coinly.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.models.CurrentUser

class MyProfileViewModel: ViewModel() {

    var currentUser = CurrentUser
        private set

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed,
     * but for notifications, where String is the type
     */
    fun updateNotifications(recyclerViewList: RecyclerView, context: Context?) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = NotificationsRecyclerViewAdapter(currentUser.instance!!.notifications, context!!)
    }
}