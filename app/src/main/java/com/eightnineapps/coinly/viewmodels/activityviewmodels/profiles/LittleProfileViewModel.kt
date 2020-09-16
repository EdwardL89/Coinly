package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.helpers.ImageUploadHelper
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import kotlinx.android.synthetic.main.fragment_little_profile.*
import java.io.ByteArrayOutputStream

class LittleProfileViewModel: ViewModel() {

    private lateinit var setPrizesRecyclerView: RecyclerView
    private lateinit var claimedPrizesRecyclerView: RecyclerView
    private val currentUserInstance = CurrentUser.instance
    private var pictureOfNewPrizeSetByteData = ByteArrayOutputStream().toByteArray()
    lateinit var observedUserInstance: User
    private val imageUploadHelper = ImageUploadHelper()

    /**
     * Removes the observed Big and navigates to the previous page
     *
     */
    fun removeLittleAndSendBack(context: Context) {
        currentUserInstance!!.numOfLittles -= 1
        Firestore.update(currentUserInstance!!, "numOfLittles", currentUserInstance.numOfLittles.toString())
        Firestore.removeLittle(currentUserInstance.email!!, observedUserInstance.email!!)

        observedUserInstance.numOfBigs -= 1
        Firestore.update(observedUserInstance, "numOfBigs", observedUserInstance.numOfBigs.toString())
        Firestore.removeBig(observedUserInstance.email!!, currentUserInstance.email!!)

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

    /**
     * Uploads image to storage nad updates the user's Uri
     */
    fun uploadNewSetPrize(prizeTitle: String, prizePrice: Int, context: Context) {
        val prizeId = imageUploadHelper.generateId()
        val prizePath = "set_prizes/${currentUserInstance!!.id}/${observedUserInstance.id}/$prizeId"
        ImgStorage.insert(pictureOfNewPrizeSetByteData, prizePath).addOnSuccessListener {
            ImgStorage.read(prizePath).addOnSuccessListener {
                uri -> Firestore.setNewPrize(observedUserInstance.email!!, currentUserInstance.email!!, Prize(prizeTitle, prizePrice, uri.toString(), prizeId))
                .addOnCompleteListener {
                    updateSetRecyclerViewAdapterAndLayoutManager(context)
                    Toast.makeText(context, "Prize Set!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Load the "Claimed prizes from you" recycler view with the appropriate prize items
     */
    fun loadClaimedPrizes(recyclerView: RecyclerView, context: Context) {
        claimedPrizesRecyclerView = recyclerView
        claimedPrizesRecyclerView.removeAllViews()
        updateClaimedRecyclerViewAdapterAndLayoutManager(context)
    }

    /**
     * Load the "Your set prizes" recycler view with the appropriate prize items
     */
    fun loadSetPrizes(recyclerView: RecyclerView, context: Context) {
        setPrizesRecyclerView = recyclerView
        setPrizesRecyclerView.removeAllViews()
        updateSetRecyclerViewAdapterAndLayoutManager(context)
    }

    /**
     * Assigns the "Claimed prizes from you" recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateClaimedRecyclerViewAdapterAndLayoutManager(context: Context?) {
        claimedPrizesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Firestore.getPrizesClaimed(observedUserInstance.email!!, currentUserInstance!!.email!!).get().addOnSuccessListener {
            val allPrizesClaimed = mutableListOf<Prize>()
            for (document in it) {
                allPrizesClaimed.add(document.toObject(Prize::class.java))
            }
            claimedPrizesRecyclerView.adapter = PrizesRecyclerViewAdapter(allPrizesClaimed, context!!, PrizeTapLocation.LITTLE_PRIZES_CLAIMED, currentUserInstance, observedUserInstance)
            if (allPrizesClaimed.isNotEmpty()) {
                (context as Activity).no_prizes_claimed_from_you_image.visibility = View.INVISIBLE
            } else {
                (context as Activity).no_prizes_claimed_from_you_image.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Assigns the "Your set prizes" recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateSetRecyclerViewAdapterAndLayoutManager(context: Context?) {
        setPrizesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Firestore.getPrizesSet(observedUserInstance.email!!, currentUserInstance!!.email!!).get().addOnSuccessListener {
            val allPrizesSet = mutableListOf<Prize>()
            for (document in it) {
                allPrizesSet.add(document.toObject(Prize::class.java))
            }
            setPrizesRecyclerView.adapter = PrizesRecyclerViewAdapter(allPrizesSet, context!!, PrizeTapLocation.LITTLE_PRIZES_SET, currentUserInstance, observedUserInstance)
            if (allPrizesSet.isNotEmpty()) {
                (context as Activity).no_prizes_set_image.visibility = View.INVISIBLE
            } else {
                (context as Activity).no_prizes_set_image.visibility = View.VISIBLE
            }
        }
    }
}