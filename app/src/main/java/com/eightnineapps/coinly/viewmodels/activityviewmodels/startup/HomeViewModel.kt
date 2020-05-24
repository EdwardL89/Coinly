package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser

class HomeViewModel: ViewModel() {

    fun setCurrentUser(intent: Intent) {
        CurrentUser.instance = intent.getSerializableExtra("current_user") as User
    }

}