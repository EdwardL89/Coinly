package com.eightnineapps.coinly.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.fragments.HomeFragments.Companion.allBigs
import com.eightnineapps.coinly.fragments.HomeFragments.Companion.allLittles

/**
 * Represents a single user of the app
 */
class UserProfileActivity : AppCompatActivity() {

    private lateinit var displayName: TextView
    private lateinit var addAsBigButton: Button
    private lateinit var addAsLittleButton: Button
    private lateinit var profilePicture: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        instantiateUIElements()
        populateUIElements()
        filterButtons()
        setupButtons()
    }

    private fun setupButtons() {
        addAsBigButton.setOnClickListener {

        }
        addAsLittleButton.setOnClickListener {

        }
    }

    /**
     * Hides the add as little/big buttons if the current user is already a big/little for the
     * observed user
     */
    private fun filterButtons() {
        val observedUserDisplayName = displayName.text.toString()
        hideOrShowAddBigButton(observedUserDisplayName)
        hideOrShowAddLittleButton(observedUserDisplayName)
    }

    /**
     * Hides the add as big button if the observed user is already a big of the current user
     */
    private fun hideOrShowAddBigButton(observedUserDisplayName: String) {
        val searchResult = allBigs.find { it.data?.get("displayName") == observedUserDisplayName }
        if (searchResult != null) addAsBigButton.visibility =  View.INVISIBLE
        else  addAsBigButton.visibility =  View.VISIBLE
    }

    /**
     * Hides the add as little button if the observed user is already a little of the current user
     */
    private fun hideOrShowAddLittleButton(observedUserDisplayName: String) {
        val searchResult = allLittles.find { it.data?.get("displayName") == observedUserDisplayName }
        if (searchResult != null) addAsLittleButton.visibility =  View.INVISIBLE
        else  addAsLittleButton.visibility =  View.VISIBLE
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
