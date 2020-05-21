package com.eightnineapps.coinly.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.models.CurrentUser

class MyProfileViewModel: ViewModel() {

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed,
     * but for notifications, where String is the type
     */
    fun updateNotifications(recyclerViewList: RecyclerView, context: Context?) {
        recyclerViewList.layoutManager = LinearLayoutManager(context)
        recyclerViewList.adapter = NotificationsRecyclerViewAdapter(getNotifications().value!!, context!!)
    }

    fun getUserBio() = CurrentUser.bio

    fun getUserCoins() = CurrentUser.coins

    fun getDisplayName() = CurrentUser.displayName

    fun getNumberOfBigs() = CurrentUser.numberOfBigs

    fun getPrizesClaimed() = CurrentUser.prizesClaimed

    fun getNotifications() = CurrentUser.notifications

    fun getNumberOfLittles() = CurrentUser.numberOfLittles

    fun getProfilePictureUri() = CurrentUser.profilePictureUri
}