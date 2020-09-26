package com.eightnineapps.coinly.viewmodels.activityviewmodels.startup

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser

class HomeViewModel: ViewModel() {

    fun setCurrentUser(intent: Intent) {
        CurrentUser.instance = intent.getSerializableExtra("current_user") as User
        val currentUserInstance = CurrentUser.instance
        CurrentUser.coins.value = currentUserInstance!!.coins
        CurrentUser.numberOfBigs.value = currentUserInstance.numOfBigs
        CurrentUser.numberOfLittles.value = currentUserInstance.numOfLittles
        CurrentUser.bio.value = currentUserInstance.bio
        CurrentUser.realName.value = currentUserInstance.realName
        CurrentUser.displayName.value = currentUserInstance.displayName
        CurrentUser.profilePictureUri.value = currentUserInstance.profilePictureUri
    }

}