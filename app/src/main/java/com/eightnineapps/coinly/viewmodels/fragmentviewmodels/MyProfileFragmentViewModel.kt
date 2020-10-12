package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class MyProfileFragmentViewModel: ViewModel() {

    private var hasLoadedNotifications = false
    private var notifications = mutableListOf<Notification>()
    private var notificationsQueryTask: Task<QuerySnapshot>? = null
    private var recyclerAdapter: NotificationsRecyclerViewAdapter? = null

    /**
     * Returns the recycler's adapter
     */
    fun getAdapter() = recyclerAdapter!!

    /**
     * Determines whether or not the the notifications have been compiled to the list
     */
    fun hasLoadedNotifications() = hasLoadedNotifications

    /**
     * Returns the query task
     */
    fun getAllNotificationsQuery() = notificationsQueryTask

    /**
     * Removes the given notifications from the list
     */
    fun removeNotifications(notificationsToBeRemoved: MutableList<String>) {
        notifications.removeAll { it.id in notificationsToBeRemoved }
        recyclerAdapter?.notifyDataSetChanged()
    }

    /**
     * Instantiates the adapter for the recycler
     */
    fun createAdapter() {
        recyclerAdapter = NotificationsRecyclerViewAdapter(notifications)
    }

    /**
     * Initiates the query for all the user's notifications
     */
    fun startQueryForNotifications() {
        notificationsQueryTask = Firestore.getNotifications(CurrentUser.instance!!.email!!).get()
    }

    /**
     * Saves all queried notifications
     */
    fun compileNotificationsToList(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            notifications.add(document.toObject(Notification::class.java))
        }
        hasLoadedNotifications = true
    }
}