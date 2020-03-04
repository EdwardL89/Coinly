package com.eightnineapps.coinly.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.User
import com.google.firebase.database.FirebaseDatabase
import kotlin.random.Random

/**
 * Creates a new user
 */
class ProfileActivity : AppCompatActivity() {

    /**
     * Must be late initialized because we haven't set the content view yet in the onCreate method
     */
    private lateinit var doneButton: Button
    private lateinit var realNameEditText: EditText
    private lateinit var displayNameEditText: EditText

    /**
     * Placed in a companion object to allow access to a single instance to all other activities
     */
    companion object {
        private const val ID_LENGTH = 30
        private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val database = FirebaseDatabase.getInstance().reference
    }

    /**
     * Initializes what's required for the user to create their profile
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        doneButton = findViewById(R.id.done_button)
        realNameEditText = findViewById(R.id.real_name_editText)
        displayNameEditText = findViewById(R.id.display_name_editText)
        setupDoneButton()
    }

    /**
     * Creates a new user, writes it to the database, then navigates to the home page
     */
    private fun setupDoneButton() {
        doneButton.setOnClickListener {
            val newUser = createNewUser()
            addUserToFirebaseDatabase(newUser)
            goToHomePage(newUser)
        }
    }

    /**
     * Grabs the name information from the UI to create a new user
     */
    private fun createNewUser(): User {
        val realName = realNameEditText.text.toString()
        val displayName = displayNameEditText.text.toString()
        val id = generateId()
        return User(realName, displayName, id)
    }

    /**
     * Writes a new user to the database
     */
    private fun addUserToFirebaseDatabase(user: User) {
        database.child("users").child(user.id).setValue(user)
    }

    /**
     * Launches an intent to go to the home page activity
     */
    private fun goToHomePage(user: User) {
        startActivity(Intent(this, HomeActivity::class.java).putExtra("currentUser", user.id))
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
