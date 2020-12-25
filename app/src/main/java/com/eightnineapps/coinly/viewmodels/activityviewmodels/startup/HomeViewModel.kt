package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser

class HomeViewModel: ViewModel() {

    /**
     * Sets the current user object to the singleton to be used throughout the app
     */
    fun setCurrentUser(intent: Intent) {
        CurrentUser.setUserInstance(intent.getSerializableExtra("current_user") as User)
        CurrentUser.instantiateLiveData()
    }
}