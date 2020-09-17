package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore

class MyProfileFragmentViewModel: ViewModel() {

    var currentUser = CurrentUser
        private set

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed,
     * but for notifications, where String is the type
     */
    fun updateNotifications(recyclerViewList: RecyclerView, context: Context?) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        Firestore.getNotifications(currentUser.instance!!).get().addOnSuccessListener {
            val allNotifications = mutableListOf<Notification>()
            for (document in it) {
                allNotifications.add(document.toObject(Notification::class.java))
            }
            recyclerViewList.adapter = NotificationsRecyclerViewAdapter(allNotifications, context!!)
        }
    }
}