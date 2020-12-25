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
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    private val ADD_AS_BIG_MESSAGE_TEMPLATE = "${CurrentUser.displayName.value} wants to add you as a big!"
    private val ADD_AS_LITTLE_MESSAGE_TEMPLATE = "${CurrentUser.displayName.value} wants to add you as a little!"

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
            .whereEqualTo("addingToUserEmail", observedUserInstance.email!!)
            .limit(1).get()
    }

    /**
     * Checks if the current user has already received a request to be added as a little
     * by the observed user
     */
    fun queryForReceivedRequestFromLittle(): Task<QuerySnapshot> {
        return Firestore.getNotifications(CurrentUser.getEmail()!!)
            .whereEqualTo("type", NotificationType.ADDING_AS_BIG)
            .whereEqualTo("addingToUserEmail", observedUserInstance.email!!)
            .limit(1).get()
    }

    /**
     * Sends an "add as little" notification to the observed user
     */
    fun sendAddLittleNotification() {
        val newNotification = constructNotification(false)
        Firestore.addNotification(observedUserInstance.email!!, newNotification)
    }

    /**
     * Sends an "add as big" notification to the observed user
     */
    fun sendAddBigNotification() {
        val newNotification = constructNotification(true)
        Firestore.addNotification(observedUserInstance.email!!, newNotification)
    }

    /**
     * Executes the given notification and removes it from the current user's notification list
     */
    fun executeAndUpdateNotification(notificationSnapshot: DocumentSnapshot) {
        val notification = notificationSnapshot.toObject(Notification::class.java)!!
        notification.execute()
        Firestore.removeNotification(CurrentUser.getEmail()!!, notification.id)
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
        newNotification.profilePictureUri = CurrentUser.profilePictureUri.value!!
        newNotification.message = "${CurrentUser.displayName.value} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        newNotification.moreInformation = "${CurrentUser.displayName.value} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        return newNotification
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }
}