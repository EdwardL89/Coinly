package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore

class BigProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private val currentUserInstance = CurrentUser.instance

    /**
     * Removes the observed Big and navigates to the previous page
     */
    fun removeBigAndSendBack(context: Context) {
        currentUserInstance!!.numOfBigs -= 1
        Firestore.update(currentUserInstance!!, "numOfBigs", currentUserInstance.numOfBigs.toString())
        Firestore.removeBig(currentUserInstance.email!!, observedUserInstance.email!!)

        observedUserInstance.numOfLittles -= 1
        Firestore.update(observedUserInstance, "numOfLittles", observedUserInstance.numOfLittles.toString())
        Firestore.removeLittle(observedUserInstance.email!!, currentUserInstance.email!!)

        Toast.makeText(context, "Removed ${observedUserInstance.displayName} as a big", Toast.LENGTH_SHORT).show()
        //TODO: add a listener to the bigs recycler view to refresh it after the removal
        (context as Activity).finish()
    }

}