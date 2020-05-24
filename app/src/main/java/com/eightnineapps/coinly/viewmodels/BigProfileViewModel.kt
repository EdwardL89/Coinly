package com.eightnineapps.coinly.viewmodels

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore

class BigProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private val currentUserInstance = CurrentUser.instance

    /**
     * Removes the observed Big and navigates to the previous page
     */
    fun removeBigAndSendBack(context: Context) {
        currentUserInstance!!.bigs.remove(observedUserInstance.email)
        Firestore.updateList(currentUserInstance, "bigs", currentUserInstance.bigs)
        Firestore.read(observedUserInstance).get().addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                val mostUpdatedObservedUser = task.result!!.toObject(User::class.java)!!
                val successfullyRemoved = mostUpdatedObservedUser.littles.remove(currentUserInstance.email.toString())
                if (successfullyRemoved) Firestore.updateList(currentUserInstance, "littles", mostUpdatedObservedUser.littles)
            }
        }
        Toast.makeText(context, "Removed ${observedUserInstance.displayName} as a big", Toast.LENGTH_SHORT).show()
        //TODO: add a listener to the bigs recycler view to refresh it after the removal
        (context as Activity).finish()
    }

}