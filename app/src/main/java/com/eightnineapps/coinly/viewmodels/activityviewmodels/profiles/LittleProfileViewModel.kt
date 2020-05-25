package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.classes.dialogues.SetNewPrizeDialogue
import com.eightnineapps.coinly.classes.helpers.ImageUploadHelper
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.models.ImgStorage
import java.io.ByteArrayOutputStream

class LittleProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private val currentUserInstance = CurrentUser.instance
    private var pictureOfNewPrizeSetByteData = ByteArrayOutputStream().toByteArray()
    val imageUploadHelper = ImageUploadHelper()

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

    /**
     * Loads selected image to an image view and prepares the image to be uploaded to Storage
     */
    fun handleGallerySelectionCompletion(requestCode: Int, resultCode: Int, data: Intent?, appContext: Context, context: Context, profilePictureImgView: ImageView) {
        if (requestCode == 1 && resultCode == AppCompatActivity.RESULT_OK) {
            Glide.with(appContext).load(data!!.data).into(profilePictureImgView)
            pictureOfNewPrizeSetByteData = imageUploadHelper.prepareForFirebaseStorageUpload(data, context)
            openDialogue(context)
        }
    }

    /**
     * Open a dialogue for the user to set the title and price of the new prize
     */
    private fun openDialogue(context: Context) {
        val dialog = SetNewPrizeDialogue()
        dialog.show((context as FragmentActivity).supportFragmentManager, "Set New Prize Dialog")
    }

    /**
     * Uploads image to storage nad updates the user's Uri
     */
    private fun uploadNewSetPrize() {
        ImgStorage.insert(pictureOfNewPrizeSetByteData, "set_prizes/${currentUserInstance!!.id}/${observedUserInstance.id}/${imageUploadHelper.generateId()}").addOnSuccessListener {
            ImgStorage.read(currentUserInstance).addOnSuccessListener {
                    uri -> currentUserInstance.prizesSet.add(uri.toString())
            }
        }
    }
}