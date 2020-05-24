package com.eightnineapps.coinly.viewmodels

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore

class LittleProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private val currentUserInstance = CurrentUser.instance

    /**
     * Removes the observed Big and navigates to the previous page
     */
    fun removeLittleAndSendBack(context: Context) {
        currentUserInstance!!.littles.remove(observedUserInstance.email)
        Firestore.updateList(currentUserInstance, "littles", currentUserInstance.littles)
        Firestore.read(observedUserInstance).get().addOnCompleteListener {
                task ->
            if (task.isSuccessful) {
                val mostUpdatedObservedUser = task.result!!.toObject(User::class.java)!!
                val successfullyRemoved = mostUpdatedObservedUser.bigs.remove(currentUserInstance.email.toString())
                if (successfullyRemoved) Firestore.updateList(currentUserInstance, "bigs", mostUpdatedObservedUser.bigs)
            }
        }
        Toast.makeText(context, "Removed ${observedUserInstance.displayName} as a little", Toast.LENGTH_SHORT).show()
        //TODO: add a listener to the bigs recycler view to refresh it after the removal
        (context as Activity).finish()
    }

}