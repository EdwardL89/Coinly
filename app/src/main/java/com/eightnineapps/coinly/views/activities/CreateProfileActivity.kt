package com.eightnineapps.coinly.views.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.views.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.views.activities.LoginActivity.Companion.TAG
import com.eightnineapps.coinly.views.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.ImageUploader
import com.eightnineapps.coinly.classes.User
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_create_profile.*
import java.io.ByteArrayOutputStream
import kotlin.random.Random
import kotlin.system.exitProcess


/**
 * Creates a new user
 */
class CreateProfileActivity : ImageUploader() {

    private var IMAGE_SELECTION_SUCCESS = 1

    /**
     * Must be late initialized because we haven't set the content view yet in the onCreate method
     */
    private lateinit var thisUser: User
    private lateinit var userProfilePictureByteData: ByteArray

    /**
     * Placed in a companion object to allow access to a single instance to all other activities
     */
    companion object {
        private const val ID_LENGTH = 30
        private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val imageStorage = Firebase.storage
    }

    /**
     * Initializes what's required for the user to create their profile
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        addCoinlyActionBarTitle()

        userProfilePictureByteData = ByteArrayOutputStream().toByteArray()

        setupDoneButton()
        setupAddProfilePictureButton()
    }

    /**
     * Close the app when the user hits "back"
     */
    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        exitProcess(0)
    }

    /**
     * Catches the result of the intent that opens the gallery to select a profile picture image
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_SELECTION_SUCCESS && resultCode == RESULT_OK) {
            Glide.with(applicationContext).load(data!!.data).into(user_profile_picture)
            userProfilePictureByteData = prepareForFirebaseStorageUpload(data)
        }
    }

    /**
     * Sets the title of the action bar to the app name in the custom font through an image view
     */
    @SuppressLint("InflateParams")
    private fun addCoinlyActionBarTitle() {
        val actionBar = this.supportActionBar!!
        actionBar.setDisplayShowCustomEnabled(true)
        actionBar.setDisplayShowTitleEnabled(false)
        val v: View = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        actionBar.customView = v
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
    }

    /**
     * Opens the gallery to select a profile picture image
     */
    private fun setupAddProfilePictureButton() {
        add_profile_picture_button.setOnClickListener {
            chooseImageFromGallery()
        }
    }

    /**
     * Creates a new user, writes it to the database, then navigates to the home page
     */
    private fun setupDoneButton() {
        done_button.setOnClickListener {
            if (noFieldsEmpty()) {
                thisUser = createNewUser()
                uploadUserAndGoToHome(thisUser)
            } else {
                Toast.makeText(this, "Info missing!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Uploads the profile picture to Storage, downloads and saves the image's Uri for reuse, then
     * uploads the user to the Firestore
     */
    private fun uploadUserAndGoToHome(newUser: User) {
        uploadToImageStorage(userProfilePictureByteData, newUser)
            .addOnSuccessListener {
                downloadProfilePicture(newUser)
                    .addOnSuccessListener { uri -> newUser.profilePictureUri = uri.toString()
                        uploadToFirestore(newUser)
                            .addOnSuccessListener {
                                goToHomePage() // Go to home page after we are guaranteed all tasks have completed
                            }
                            .addOnFailureListener { Log.w(TAG, "Could not upload user to Firestore") }
                    }
                    .addOnFailureListener { Log.w(TAG, "Could not download from Storage") }
            }
            .addOnFailureListener { Log.w(TAG, "Could not upload to Firebase storage") }
    }

    /**
     * Writes a new user to the database
     */
    private fun uploadToFirestore(newUser: User): Task<Void> {
        return database.collection("users").document(newUser.email.toString()).set(newUser)
    }

    /**
     * Launches an intent to go to the home page activity
     */
    private fun goToHomePage() {
        val intent = Intent(this, HomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
    }

    /**
     * Determines whether or not all fields are empty
     */
    private fun noFieldsEmpty(): Boolean {
        return userProfilePictureByteData.isNotEmpty() &&
                real_name_editText.text.toString() != "" &&
                display_name_editText.text.toString() != "" &&
                bio_edit_text.text.toString() != ""
    }

    /**
     * Grabs the name information from the UI to create a new user
     */
    private fun createNewUser(): User {
        val realName = real_name_editText.text.toString()
        val displayName = display_name_editText.text.toString()
        val bio = bio_edit_text.text.toString()
        val id = generateId()
        val email = auth.currentUser!!.email
        return User(realName, displayName, id, email, bio)
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..ID_LENGTH)
            .map { Random.nextInt(0, charPool.size) }
            .map(charPool::get)
            .joinToString("")
    }
}
