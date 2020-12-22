package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.helpers.ImageUploadHelper
import com.eightnineapps.coinly.classes.helpers.NotificationDialogCreator
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class LittleProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private var hasLoadedPrizesSet = false
    private var hasLoadedPrizesClaimed = false
    private val dialogCreator = NotificationDialogCreator()
    private var prizesSetQuery: Task<QuerySnapshot>? = null
    private var prizesClaimedQuery: Task<QuerySnapshot>? = null
    private var prizesSetAdapter: PrizesRecyclerViewAdapter? = null
    private var prizesClaimedAdapter: PrizesRecyclerViewAdapter? = null
    private val allPrizesSet = mutableListOf<Prize>()
    private val allPrizesClaimed = mutableListOf<Prize>()
    private val imageUploadHelper = ImageUploadHelper()
    private var pictureOfNewPrizeSetByteData = ByteArrayOutputStream().toByteArray()

    /**
     * Returns the adapter of the prizes set recycler view
     */
    fun getPrizesSetAdapter() = prizesSetAdapter!!

    /**
     * Returns the adapter of the prizes claimed recycler view
     */
    fun getPrizesClaimedAdapter() = prizesClaimedAdapter!!

    /**
     * Determines whether or not the prizes set have been loaded
     */
    fun hasLoadedPrizesSet() = hasLoadedPrizesSet

    /**
     * Determines whether or not the prizes claimed have been loaded
     */
    fun hasLoadedPrizesClaimed() = hasLoadedPrizesClaimed

    /**
     * Returns the query to get the prizes set
     */
    fun getPrizesSetQuery() = prizesSetQuery

    /**
     * Returns the query to get the prizes claimed
     */
    fun getPrizesClaimedQuery() = prizesClaimedQuery

    /**
     * Launches the query to get the prizes set
     */
    fun startQueryForPrizesSet() {
        prizesSetQuery = Firestore.getPrizesSet(observedUserInstance.email!!, CurrentUser.getEmail()!!).get()
    }

    /**
     * Launches the query to get the prizes claimed
     */
    fun startQueryForPrizesClaimed() {
        prizesClaimedQuery = Firestore.getPrizesClaimed(observedUserInstance.email!!, CurrentUser.getEmail()!!).get()
    }

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
            PrizeTapLocation.LITTLE_PRIZES_SET, observedUserInstance, view)
    }

    /**
     * Instantiates the adapter for the prizes claimed recycler
     */
    fun createPrizesClaimedAdapter(view: View) {
        prizesClaimedAdapter = PrizesRecyclerViewAdapter(allPrizesClaimed,
            PrizeTapLocation.LITTLE_PRIZES_CLAIMED, observedUserInstance, view)
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
     * Loads selected image to an image view and prepares the image to be uploaded to Storage
     */
    fun handleGallerySelectionCompletion(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            pictureOfNewPrizeSetByteData = imageUploadHelper.prepareForFirebaseStorageUpload(data, context)
        }
    }

    /**
     * Adds a prize item to the set prizes recycler view
     */
    fun addSetPrizeToRecycler(prize: Prize) {
        prizesSetAdapter?.addItem(prize)
    }

    /**
     * Starts a query to save a new prize in the firestore
     */
    fun savePrizeInFireStore(prize: Prize): Task<Void> {
        return Firestore.setNewPrize(observedUserInstance.email!!, CurrentUser.getEmail()!!, prize)
    }

    /**
     * Generates a random 30-character Id
     */
    fun generateId() = imageUploadHelper.generateId()

    /**
     * Generates a path to save a prize in based on the prize Id
     */
    fun generatePrizePath(prizeId: String): String {
        return "set_prizes/${CurrentUser.getEmail()}/${observedUserInstance.email}/$prizeId"
    }

    /**
     * Starts a query to save an image to storage
     */
    fun insertPrizeImageToStorage(prizePath: String): UploadTask {
        return ImgStorage.insert(pictureOfNewPrizeSetByteData, prizePath)
    }

    /**
     * Starts a query to download an image uri at the given path
     */
    fun downloadImageUri(prizePath: String): Task<Uri> {
        return ImgStorage.readImage(prizePath)
    }

    /**
     * Executes and sends a giving coins notification
     */
    fun sendAndExecuteGiveNotification(coinsGiving: Int, optionalNote: String): Task<Void> {
        val notification = constructNotification(coinsGiving, optionalNote)
        notification.execute()
        observedUserInstance.coins += coinsGiving
        return Firestore.addNotification(observedUserInstance.email!!, notification)
    }

    /**
     * Constructs the notification to be sent to the little with all the information to inform them
     * of the coins they've been given
     */
    private fun constructNotification(coinsGiving: Int, optionalNote: String): Notification {
        val notification = Notification()
        notification.id = generateId()
        notification.coins = coinsGiving
        notification.moreInformation = optionalNote
        notification.type = NotificationType.GIVING_COINS
        notification.toAddUserEmail = observedUserInstance.email!!
        notification.profilePictureUri = CurrentUser.instance!!.profilePictureUri
        notification.message = "${CurrentUser.instance!!.displayName} gave you $coinsGiving coins"
        return notification
    }

    /**
     * Removes the observed Little
     */
    fun removeLittleAndSendBack() {
        CurrentUser.littleToBeRemoved = observedUserInstance
        removeLittleFromCurrentUser()
        removeCurrentUserFromObservedUsersBigs()
    }

    /**
     * Removes the observed user from the current user's little list
     */
    private fun removeLittleFromCurrentUser() {
        CurrentUser.decrementLittles()
        Firestore.removeLittle(CurrentUser.getEmail()!!, observedUserInstance.email!!)
        Firestore.update(CurrentUser.getEmail()!!, "numOfLittles", CurrentUser.numOfLittles.value.toString())
    }

    /**
     * Removes the current user from the observed user's big list
     */
    private fun removeCurrentUserFromObservedUsersBigs() {
        observedUserInstance.numOfBigs -= 1
        Firestore.update(observedUserInstance.email!!, "numOfBigs", observedUserInstance.numOfBigs.toString())
        Firestore.removeBig(observedUserInstance.email!!, CurrentUser.getEmail()!!)
    }
}