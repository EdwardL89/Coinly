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
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import kotlinx.android.synthetic.main.fragment_little_profile.*
import java.io.ByteArrayOutputStream

class LittleProfileViewModel: ViewModel() {
    private lateinit var setPrizesRecyclerView: RecyclerView
    private val currentUserInstance = CurrentUser.instance

    private var pictureOfNewPrizeSetByteData = ByteArrayOutputStream().toByteArray()

    lateinit var observedUserInstance: User

    val imageUploadHelper = ImageUploadHelper()

    /**
     * Removes the observed Big and navigates to the previous page
     */
    fun removeLittleAndSendBack(context: Context) {
        /*currentUserInstance!!.littles.remove(observedUserInstance.email)
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
        (context as Activity).finish()*/
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
        val prizePath = "set_prizes/${currentUserInstance!!.id}/${observedUserInstance.id}/prizeId"
        ImgStorage.insert(pictureOfNewPrizeSetByteData, prizePath).addOnSuccessListener {
            ImgStorage.read(prizePath).addOnSuccessListener {
                uri -> Firestore.read(observedUserInstance)
                .collection("Bigs")
                .document(currentUserInstance.email!!)
                .collection("Prizes")
                .document(prizeId).set(Prize(prizeTitle, prizePrice, uri.toString(), prizeId)).addOnCompleteListener {
                    updateRecyclerViewAdapterAndLayoutManager(context)
                    Toast.makeText(context, "Prize Set!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun loadSetPrizes(recyclerView: RecyclerView, context: Context) {
        setPrizesRecyclerView = recyclerView
        setPrizesRecyclerView.removeAllViews()
        updateRecyclerViewAdapterAndLayoutManager(context)
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateRecyclerViewAdapterAndLayoutManager(context: Context?) {
        setPrizesRecyclerView.layoutManager = LinearLayoutManager(context)
        Firestore.read(observedUserInstance)
            .collection("Bigs")
            .document(currentUserInstance?.email!!)
            .collection("Prizes").get().addOnSuccessListener {
                val allPrizesSet = mutableListOf<Prize>()
                for (document in it) {
                    allPrizesSet.add(document.toObject(Prize::class.java))
                }
                setPrizesRecyclerView.adapter = PrizesRecyclerViewAdapter(allPrizesSet, context!!)
                if (allPrizesSet.isNotEmpty()) {
                    (context as Activity).no_prizes_set_image.visibility = View.INVISIBLE
                } else {
                    (context as Activity).no_prizes_set_image.visibility = View.VISIBLE
                }
            }
    }
}