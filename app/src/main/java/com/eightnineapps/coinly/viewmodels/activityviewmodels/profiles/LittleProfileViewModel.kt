package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.helpers.ImageUploadHelper
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream

class LittleProfileViewModel: ViewModel() {

    private var hasLoadedPrizesSet = false
    private var hasLoadedPrizesClaimed = false
    private var prizesSetQuery: Task<QuerySnapshot>? = null
    private var prizesClaimedQuery: Task<QuerySnapshot>? = null
    private var prizesSetAdapter: PrizesRecyclerViewAdapter? = null
    private var prizesClaimedAdapter: PrizesRecyclerViewAdapter? = null
    private val allPrizesSet = mutableListOf<Prize>()
    private val allPrizesClaimed = mutableListOf<Prize>()
    private val currentUserInstance = CurrentUser.instance
    private var pictureOfNewPrizeSetByteData = ByteArrayOutputStream().toByteArray()
    lateinit var observedUserInstance: User
    private val imageUploadHelper = ImageUploadHelper()

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
    fun startQueryForPrizesSet(): Task<QuerySnapshot> {
        return Firestore.getPrizesSet(observedUserInstance.email!!, currentUserInstance!!.email!!).get()
    }

    /**
     * Launches the query to get the prizes claimed
     */
    fun startQueryForPrizesClaimed(): Task<QuerySnapshot> {
        return Firestore.getPrizesClaimed(observedUserInstance.email!!, currentUserInstance!!.email!!).get()
    }

    /**
     * Instantiates the adapter for the prizes set recycler
     */
    fun createPrizesSetAdapter() {
        prizesSetAdapter = PrizesRecyclerViewAdapter(allPrizesSet,
            PrizeTapLocation.LITTLE_PRIZES_SET, observedUserInstance)
    }

    /**
     * Instantiates the adapter for the prizes claimed recycler
     */
    fun createPrizesClaimedAdapter() {
        prizesClaimedAdapter = PrizesRecyclerViewAdapter(allPrizesClaimed,
            PrizeTapLocation.LITTLE_PRIZES_CLAIMED, observedUserInstance)
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
     * Removes the observed Big and navigates to the previous page
     */
    fun removeLittleAndSendBack(context: Context) {
        CurrentUser.littleToBeRemoved = observedUserInstance

        currentUserInstance!!.numOfLittles -= 1
        Firestore.update(currentUserInstance!!, "numOfLittles", currentUserInstance.numOfLittles.toString())
        Firestore.removeLittle(currentUserInstance.email!!, observedUserInstance.email!!)

        observedUserInstance.numOfBigs -= 1
        Firestore.update(observedUserInstance, "numOfBigs", observedUserInstance.numOfBigs.toString())
        Firestore.removeBig(observedUserInstance.email!!, currentUserInstance.email!!)
        CurrentUser.numOfLittles.value = currentUserInstance.numOfLittles

        Toast.makeText(context, "Removed ${observedUserInstance.displayName} as a little", Toast.LENGTH_SHORT).show()

        (context as Activity).finish()
    }

    /**
     * Loads selected image to an image view and prepares the image to be uploaded to Storage
     */
    fun handleGallerySelectionCompletion(requestCode: Int, resultCode: Int, data: Intent?, context: Context) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            pictureOfNewPrizeSetByteData = imageUploadHelper.prepareForFirebaseStorageUpload(data, context)
        }
    }

    fun savePrizeInFireStore(prize: Prize): Task<Void> {
        return Firestore.setNewPrize(observedUserInstance.email!!, CurrentUser.getEmail()!!, prize)
    }

    fun generateId() = imageUploadHelper.generateId()

    fun generatePrizePath(prizeId: String): String {
        return "set_prizes/${currentUserInstance!!.id}/${observedUserInstance.id}/$prizeId"
    }

    fun insertPrizeImageToStorage(prizePath: String): UploadTask {
        return ImgStorage.insert(pictureOfNewPrizeSetByteData, prizePath)
    }

    fun downloadImageUri(prizePath: String): Task<Uri> {
        return ImgStorage.readImage(prizePath)
    }
}