package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.TAG
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.User
import com.google.android.gms.tasks.Task
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import kotlin.random.Random
import kotlin.system.exitProcess


/**
 * Creates a new user
 */
class CreateProfileActivity : AppCompatActivity() {

    private var IMAGE_SELECTION_SUCCESS = 1

    /**
     * Must be late initialized because we haven't set the content view yet in the onCreate method
     */
    private lateinit var thisUser: User
    private lateinit var doneButton: Button
    private lateinit var addProfilePictureButton: Button

    private lateinit var realNameEditText: EditText
    private lateinit var displayNameEditText: EditText

    private lateinit var userProfilePicture: ImageView

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

        doneButton = findViewById(R.id.done_button)
        realNameEditText = findViewById(R.id.real_name_editText)
        userProfilePicture = findViewById(R.id.user_profile_picture)
        displayNameEditText = findViewById(R.id.display_name_editText)
        addProfilePictureButton = findViewById(R.id.add_profile_picture_button)
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
            Glide.with(applicationContext).load(data!!.data).into(userProfilePicture)
            prepareForFirebaseStorageUpload(data)
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
     * Begins the process to upload the selected image to the Firebase storage reference.
     * Does not upload yet because we need to make sure a new user has been created (hitting the
     * "done" button) so we can name the image file the user's unique ID.
     */
    private fun prepareForFirebaseStorageUpload(data: Intent?) {
        val selectedImageUri = data!!.data
        val selectedImageBitmap = convertUriToBitmap(selectedImageUri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        selectedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        userProfilePictureByteData = byteArrayOutputStream.toByteArray()
    }

    /**
     * Converts a Uri to a Bitmap
     */
    private fun convertUriToBitmap(selectedImageUri: Uri?): Bitmap {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val imageSource = ImageDecoder.createSource(this.contentResolver, selectedImageUri!!)
            ImageDecoder.decodeBitmap(imageSource)
        } else {
            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImageUri)
        }
    }

    /**
     * Opens the gallery to select a profile picture image
     */
    private fun setupAddProfilePictureButton() {
        addProfilePictureButton.setOnClickListener {
            val openGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            startActivityForResult(openGallery, IMAGE_SELECTION_SUCCESS)
        }
    }

    /**
     * Creates a new user, writes it to the database, then navigates to the home page
     */
    private fun setupDoneButton() {
        doneButton.setOnClickListener {
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
     * Uploads an image's byte data to the Firebase Storage reference
     */
    private fun uploadToImageStorage(imageByteData: ByteArray, currentUser: User): UploadTask {
        return imageStorage.reference.child("profile_pictures").child(currentUser.id).putBytes(imageByteData)
    }

    /**
     * Queries the Firebase Storage reference for the user's profile picture
     */
    private fun downloadProfilePicture(newUser: User): Task<Uri> {
        return imageStorage.reference
            .child("profile_pictures")
            .child(newUser.id).downloadUrl
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
        return userProfilePictureByteData.isNotEmpty() && realNameEditText.text.toString() != "" && displayNameEditText.text.toString() != ""
    }

    /**
     * Grabs the name information from the UI to create a new user
     */
    private fun createNewUser(): User {
        val realName = realNameEditText.text.toString()
        val displayName = displayNameEditText.text.toString()
        val id = generateId()
        val email = auth.currentUser!!.email
        return User(realName, displayName, id, email)
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
