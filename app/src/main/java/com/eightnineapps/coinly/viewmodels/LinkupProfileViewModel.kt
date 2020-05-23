package com.eightnineapps.coinly.viewmodels

import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.Notification
import com.eightnineapps.coinly.classes.PrizeLoader
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

class LinkupProfileViewModel: ViewModel() {

    val prizeLoader = PrizeLoader()
    lateinit var observedUserInstance: User
    private val currentUserInstance = CurrentUser.instance!!

    fun getUpdatedObservedUser(observedUser: User) = Firestore.read(observedUser).get()

    /**
     * Get request status for current and observe user
     */
    fun getAddStatus(asBig: Boolean) = Triple(alreadyRequested(asBig), alreadyAdded(asBig), alreadyReceivedRequest(asBig))

    /**
     * Determines if the current user as already request the observed user to be added as a big/little
     */
    private fun alreadyRequested(asBig: Boolean): Boolean {
        val notificationMessageTemplate = "${currentUserInstance.displayName} wants to add you as a ${if (asBig) "big" else "little"}!"
        return observedUserInstance.notifications.map{ it.message }.contains(notificationMessageTemplate)
    }

    /**
     * Checks to see if the two users have already been added to eachother
     */
    private fun alreadyAdded(asBig: Boolean): Boolean {
        return if (asBig) {
            observedUserInstance.littles.contains(currentUserInstance.email)
        } else {
            observedUserInstance.bigs.contains(currentUserInstance.email)
        }
    }

    /**
     * Checks if the current user has already received the respective notification from the observed user
     */
    private fun alreadyReceivedRequest(asBig: Boolean): Boolean {
        return currentUserInstance.notifications.find {
            it.type == (if (asBig) NotificationType.ADDING_AS_LITTLE else NotificationType.ADDING_AS_BIG)  &&
                    it.toAddUserEmail == currentUserInstance.email &&
                    it.addingToUserEmail == currentUserInstance.email } != null
    }

    /**
     * Initiates the notification sending process by querying for the current user's name to place
     * in the string message
     */
    fun sendAddNotification(sendingToBig: Boolean) {
        val newNotification = constructNotification(sendingToBig)
        observedUserInstance.notifications.add(newNotification)
        Firestore.updateNotifications(observedUserInstance)
    }

    /**
     * Initiates the process of checking if the current user has a pending request
     */
    fun checkForPendingRequest(type: NotificationType): Pair<Notification, Boolean> {
        val notification = currentUserInstance.notifications.find {
            it.type == type && it.toAddUserEmail == currentUserInstance.email && it.addingToUserEmail == observedUserInstance.email }!!
        return if (type == NotificationType.ADDING_AS_BIG) Pair(notification, true) else Pair(notification, false)
    }

    fun executeAndUpdateNotification(notification: Notification) {
        notification.execute()
        currentUserInstance.notifications.remove(notification)
        Firestore.updateNotifications(currentUserInstance)
    }

    /**
     * Creates and returns a new notification object with its fields instantiated
     */
    private fun constructNotification(sendingToBig: Boolean): Notification {
        val newNotification = Notification()
        newNotification.type = if (sendingToBig) NotificationType.ADDING_AS_BIG else NotificationType.ADDING_AS_LITTLE
        newNotification.addingToUserEmail = currentUserInstance.email!!
        newNotification.toAddUserEmail = observedUserInstance.email!!
        newNotification.profilePictureUri = currentUserInstance.profilePictureUri
        newNotification.message = "${currentUserInstance.displayName} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        return newNotification
    }
}