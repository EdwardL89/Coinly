package com.eightnineapps.coinly.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.User

/**
 * Represents a single user of the app
 */
class UserProfileActivity : AppCompatActivity() {

    private lateinit var displayName: TextView
    private lateinit var addAsBigButton: Button
    private lateinit var profilePicture: ImageView
    private lateinit var addAsLittleButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        instantiateUIElements()
        populateUIElements()
    }

    /**
     * Instantiates the UI elements to their resource layout IDs
     */
    private fun instantiateUIElements() {
        addAsBigButton = findViewById(R.id.add_as_big_button)
        profilePicture = findViewById(R.id.user_profile_picture)
        addAsLittleButton = findViewById(R.id.add_as_little_button)
        displayName = findViewById(R.id.profile_page_display_name_textView)
    }

    /**
     * Populates the visible UI elements of this activity to their respective data for the user
     */
    private fun populateUIElements() {
        val currentUser = intent.getSerializableExtra("user_object") as User
        displayName.text = currentUser.displayName
        Glide.with(this).load(currentUser.profilePictureUri).into(profilePicture)
    }
}
