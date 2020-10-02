package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import kotlin.random.Random

class LinkupProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private var hasDeterminedConnectionStatus = false
    private val ADD_AS_BIG_MESSAGE_TEMPLATE = "${CurrentUser.displayName} wants to add you as a big!"
    private val ADD_AS_LITTLE_MESSAGE_TEMPLATE = "${CurrentUser.displayName} wants to add you as a little!"
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    /**
     * Returns whether or not the view model has determined the connection status between the
     * current and observed user
     */
    fun hasDeterminedConnectionStatus() = hasDeterminedConnectionStatus

    /**
     * Helps determine whether or not the current user is already a big of the
     * observed user
     */
    fun queryForContainedBig(): Task<DocumentSnapshot> {
        return Firestore.getBigs(observedUserInstance.email!!)
            .document(CurrentUser.getEmail()!!).get()
    }

    /**
     * Helps determine whether or not the current user is already a little of the
     * observed user
     */
    fun queryForContainedLittle(): Task<DocumentSnapshot> {
        return Firestore.getLittles(observedUserInstance.email!!)
            .document(CurrentUser.getEmail()!!).get()
    }

    /**
     * Checks if the observed user has already received a request to be added as a big
     * by the current user
     */
    fun queryForAlreadyRequestedBig(): Task<QuerySnapshot> {
        return Firestore.getNotifications(observedUserInstance.email!!)
            .whereEqualTo("message", ADD_AS_BIG_MESSAGE_TEMPLATE).get()
    }

    /**
     * Checks if the observed user has already received a request to be added as a little
     * by the current user
     */
    fun queryForAlreadyRequestedLittle(): Task<QuerySnapshot> {
        return Firestore.getNotifications(observedUserInstance.email!!)
            .whereEqualTo("message", ADD_AS_LITTLE_MESSAGE_TEMPLATE).get()
    }

    /**
     * Checks if the current user has already received a request to be added as a big
     * by the observed user
     */
    fun queryForReceivedRequestFromBig(): Task<QuerySnapshot> {
        return Firestore.getNotifications(CurrentUser.getEmail()!!)
            .whereEqualTo("type", NotificationType.ADDING_AS_LITTLE)
            .whereEqualTo("addingToUserEmail", observedUserInstance.email!!).get()
    }

    /**
     * Checks if the current user has already received a request to be added as a little
     * by the observed user
     */
    fun queryForReceivedRequestFromLittle(): Task<QuerySnapshot> {
        return Firestore.getNotifications(CurrentUser.getEmail()!!)
            .whereEqualTo("type", NotificationType.ADDING_AS_BIG)
            .whereEqualTo("addingToUserEmail", observedUserInstance.email!!).get()
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
        Firestore.removeNotification(CurrentUser.getEmail()!!, notification)
    }

    /**
     * Creates and returns a new notification object with its fields instantiated
     */
    private fun constructNotification(sendingToBig: Boolean): Notification {
        val newNotification = Notification()
        newNotification.id = generateId()
        newNotification.type = if (sendingToBig) NotificationType.ADDING_AS_BIG else NotificationType.ADDING_AS_LITTLE
        newNotification.addingToUserEmail = CurrentUser.getEmail()!!
        newNotification.toAddUserEmail = observedUserInstance.email!!
        newNotification.profilePictureUri = CurrentUser.profilePictureUri.toString()
        newNotification.message = "${CurrentUser.displayName.toString()} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        newNotification.moreInformation = "${CurrentUser.displayName.toString()} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        return newNotification
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }
}