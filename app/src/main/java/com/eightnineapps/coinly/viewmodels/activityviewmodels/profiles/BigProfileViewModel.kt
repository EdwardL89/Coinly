package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.helpers.NotificationDialogCreator
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlin.random.Random

class BigProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private var hasLoadedPrizesSet = false
    private var hasLoadedPrizesClaimed = false
    private val allPrizesSet = mutableListOf<Prize>()
    private val allPrizesClaimed = mutableListOf<Prize>()
    private val dialogCreator = NotificationDialogCreator()
    private var prizesSetQuery: Task<QuerySnapshot>? = null
    private var prizesClaimedQuery: Task<QuerySnapshot>? = null
    private var prizesSetAdapter: PrizesRecyclerViewAdapter? = null
    private var prizesClaimedAdapter: PrizesRecyclerViewAdapter? = null
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    /**
     * Determines whether or not the prizes set have been loaded
     */
    fun hasLoadedPrizesSet() = hasLoadedPrizesSet

    /**
     * Determines whether or not the prizes claimed have been loaded
     */
    fun hasLoadedPrizesClaimed() = hasLoadedPrizesClaimed

    /**
     * Returns the adapter of the prizes set recycler view
     */
    fun getPrizesSetAdapter() = prizesSetAdapter!!

    /**
     * Returns the adapter of the prizes claimed recycler view
     */
    fun getPrizesClaimedAdapter() = prizesClaimedAdapter!!

    /**
     * Returns the query to get the prizes set
     */
    fun getPrizesSetQuery() = prizesSetQuery

    /**
     * Returns the query to get the prizes claimed
     */
    fun getPrizesClaimedQuery() = prizesClaimedQuery

    /**
     * Opens a dialog to confirm the removal of the big
     */
    fun openConfirmationDialog(context: Context): AlertDialog {
        val alertDialog = dialogCreator.createConfirmationDialog(observedUserInstance, context)
        dialogCreator.showDialog(alertDialog)
        return alertDialog
    }

    /**
     * Instantiates the adapter for the prizes set recycler
     */
    fun createPrizesSetAdapter(view: View) {
        prizesSetAdapter = PrizesRecyclerViewAdapter(allPrizesSet,
            PrizeTapLocation.BIG_PRIZES_SET, observedUserInstance, view)
    }

    /**
     * Instantiates the adapter for the prizes claimed recycler
     */
    fun createPrizesClaimedAdapter(view: View) {
        prizesClaimedAdapter = PrizesRecyclerViewAdapter(allPrizesClaimed,
            PrizeTapLocation.BIG_PRIZES_CLAIMED, observedUserInstance, view)
    }

    /**
     * Launches the query to get the prizes set
     */
    fun startQueryForPrizesSet() {
        prizesSetQuery = Firestore.getPrizesSet(CurrentUser.getEmail()!!, observedUserInstance.email!!).get()
    }

    /**
     * Launches the query to get the prizes claimed
     */
    fun startQueryForClaimedPrizes() {
        prizesClaimedQuery = Firestore.getPrizesClaimedFromBig(CurrentUser.getEmail()!!, observedUserInstance.email!!).get()
    }

    /**
     * Saves all the prizes set retrieved from the query
     */
    fun compilePrizesSet(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allPrizesSet.add(document.toObject(Prize::class.java))
        }
        hasLoadedPrizesSet = true
    }

    /**
     * Saves all the prizes claimed retrieved from the query
     */
    fun compilePrizesClaimed(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allPrizesClaimed.add(document.toObject(Prize::class.java))
        }
        hasLoadedPrizesClaimed = true
    }

    /**
     * Sends a request notification to the observed user
     */
    fun sendNotification(coinsRequesting: Int, reasonForRequest: String) {
        val notification = constructRequestNotification(coinsRequesting, reasonForRequest)
        Firestore.addNotification(observedUserInstance.email!!, notification)
    }

    /**
     * Removes the observed Big
     */
    fun removeBigAndSendBack() {
        CurrentUser.bigToBeRemoved = observedUserInstance
        removeBigFromCurrentUser()
        removeCurrentUserFromObservedUsersLittles()

    }

    /**
     * Removes the observed user from the current user's big list
     */
    private fun removeBigFromCurrentUser() {
        CurrentUser.decrementBigs()
        Firestore.removeBig(CurrentUser.getEmail()!!, observedUserInstance.email!!)
        Firestore.update(CurrentUser.getEmail()!!, "numOfBigs", CurrentUser.numOfBigs.value.toString())
    }

    /**
     * Removes the current user from the observed user's little list
     */
    private fun removeCurrentUserFromObservedUsersLittles() {
        observedUserInstance.numOfLittles -= 1
        Firestore.update(observedUserInstance.email!!, "numOfLittles", observedUserInstance.numOfLittles.toString())
        Firestore.removeLittle(observedUserInstance.email!!, CurrentUser.getEmail()!!)
    }

    /**
     * Constructs and returns a notification that holds information about a coin request
     */
    private fun constructRequestNotification(coinsRequesting: Int, reasonForRequest: String): Notification {
        val notification = Notification()
        notification.id = generateId()
        val myDisplayName = CurrentUser.instance!!.displayName
        notification.coins = coinsRequesting
        notification.type = NotificationType.REQUESTING_COINS
        notification.message = "$myDisplayName requested $coinsRequesting coins"
        notification.moreInformation = "$myDisplayName is requesting $coinsRequesting coins. \n\n Reason:\n$reasonForRequest"
        notification.profilePictureUri = CurrentUser.instance!!.profilePictureUri
        notification.addingToUserEmail = CurrentUser.instance!!.email!!
        return notification
    }


    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }
}