package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging

class HomeViewModel: ViewModel() {

    /**
     * Sets the current user object to the singleton to be used throughout the app
     */
    fun setCurrentUser(intent: Intent) {
        CurrentUser.setUserInstance(intent.getSerializableExtra("current_user") as User)
        CurrentUser.instantiateLiveData()
    }

    /**
     * Retrieves the token used for the current user's cloud function
     */
    fun retrieveCloudToken(): Task<String> {
        return FirebaseMessaging.getInstance().token
    }
}