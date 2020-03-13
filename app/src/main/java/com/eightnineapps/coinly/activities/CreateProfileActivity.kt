package com.eightnineapps.coinly.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.User
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
    private lateinit var doneButton: Button
    private lateinit var addProfilePictureButton: Button

    private lateinit var realNameEditText: EditText
    private lateinit var displayNameEditText: EditText

    /**
     * Placed in a companion object to allow access to a single instance to all other activities
     */
    companion object {
        private const val ID_LENGTH = 30
        private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    }

    /**
     * Initializes what's required for the user to create their profile
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_profile)
        doneButton = findViewById(R.id.done_button)
        addProfilePictureButton = findViewById(R.id.add_profile_picture_button)

        realNameEditText = findViewById(R.id.real_name_editText)
        displayNameEditText = findViewById(R.id.display_name_editText)

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
        if (requestCode == IMAGE_SELECTION_SUCCESS && resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap? = data!!.extras!!.getParcelable("data")
        }
    }

    /**
     * Opens the gallery to select a profile picture image
     */
    private fun setupAddProfilePictureButton() {
        addProfilePictureButton.setOnClickListener {
            val openGalleryIntent = Intent()
            openGalleryIntent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT;
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), IMAGE_SELECTION_SUCCESS);
        }
    }

    /**
     * Creates a new user, writes it to the database, then navigates to the home page
     */
    private fun setupDoneButton() { //TODO: Don't let the user hit done if there are empty fields
        doneButton.setOnClickListener {
            val newUser = createNewUser()
            addUserToFirebaseDatabase(newUser)
            goToHomePage()
        }
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
     * Writes a new user to the database
     */
    private fun addUserToFirebaseDatabase(user: User) {
        user.email?.let { database.collection("users").document(it).set(user) }
    }

    /**
     * Launches an intent to go to the home page activity
     */
    private fun goToHomePage() {
        startActivity(Intent(this, HomeActivity::class.java))
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
