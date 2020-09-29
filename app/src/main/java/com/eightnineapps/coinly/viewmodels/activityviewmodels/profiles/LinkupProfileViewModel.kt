package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlin.random.Random

class LinkupProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    var observedUserInstanceLittles = mutableListOf<String>()
    var observedUserInstanceBigs = mutableListOf<String>()
    private val currentUserInstance = CurrentUser.instance!!
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun getUpdatedObservedUser(observedUser: User) = Firestore.read(observedUser.email!!).get()

    /**
     * Checks to see if the two users have already been added to eachother
     */
    fun alreadyAdded(asBig: Boolean): Boolean {
        return if (asBig) {
            observedUserInstanceLittles.contains(currentUserInstance.email)
        } else {
            observedUserInstanceBigs.contains(currentUserInstance.email)
        }
    }

    /**
     * Initiates the notification sending process by querying for the current user's name to place
     * in the string message
     */
    fun sendAddNotification(sendingToBig: Boolean) {
        val newNotification = constructNotification(sendingToBig)
        Firestore.addNotification(observedUserInstance.email!!, newNotification)
    }

    fun executeAndUpdateNotification(notification: Notification) {
        notification.execute()
        Firestore.removeNotification(currentUserInstance.email!!, notification)
    }

    /**
     * Creates and returns a new notification object with its fields instantiated
     */
    private fun constructNotification(sendingToBig: Boolean): Notification {
        val newNotification = Notification()
        newNotification.id = generateId()
        newNotification.type = if (sendingToBig) NotificationType.ADDING_AS_BIG else NotificationType.ADDING_AS_LITTLE
        newNotification.addingToUserEmail = currentUserInstance.email!!
        newNotification.toAddUserEmail = observedUserInstance.email!!
        newNotification.profilePictureUri = currentUserInstance.profilePictureUri
        newNotification.message = "${currentUserInstance.displayName} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        newNotification.moreInformation = "${currentUserInstance.displayName} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        return newNotification
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }

    fun retrieveObservedUserBigs(): Task<QuerySnapshot> {
        return Firestore.getBigs(observedUserInstance.email!!).get()
    }

    fun retrieveObservedUserLittles(): Task<QuerySnapshot> {
        return Firestore.getLittles(observedUserInstance.email!!).get()
    }
}