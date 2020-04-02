package com.eightnineapps.coinly.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.fragments.HomeFragments.Companion.allBigs
import com.eightnineapps.coinly.fragments.HomeFragments.Companion.allLittles
import com.eightnineapps.coinly.fragments.HomeFragments.Companion.currentUserSnapshot
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot

/**
 * Represents a single user of the app
 */
class UserProfileActivity : AppCompatActivity() {

    private lateinit var displayName: TextView
    private lateinit var addAsBigButton: Button
    private lateinit var addAsLittleButton: Button
    private lateinit var profilePicture: ImageView
    private lateinit var ObservedUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        instantiateUIElements()
        populateUIElements()
        filterButtons()
        setupButtons()
    }

    /**
     * Set the onClick listeners for the "Add as" button
     */
    private fun setupButtons() {
        setUpAddAsBig()
        setUpAddAsLittle()
    }

    /**
     * Sets up the "add as little" button and determines whether it should be enabled
     */
    private fun setUpAddAsLittle() {
        if (!alreadyRequested(false)) {
            addAsLittleButton.setOnClickListener {
                sendAddNotification(false)
                addAsLittleButton.isEnabled = false
            }
        } else {
            addAsLittleButton.isEnabled = false
        }
    }

    /**
     * Sets up the "add as big" button and determines whether it should be enabled
     */
    private fun setUpAddAsBig() {
        if (!alreadyRequested(true)) {
            addAsBigButton.setOnClickListener {
                sendAddNotification(true)
                addAsBigButton.isEnabled = false
            }
        } else {
            addAsBigButton.isEnabled = false
        }
    }

    /**
     * Determines if the current user as already request the observed user to be added as a big/little
     */
    private fun alreadyRequested(asBig: Boolean): Boolean {
        val currentUserDisplayName=  currentUserSnapshot.get("displayName") as String
        return ObservedUser.notifications.contains("$currentUserDisplayName wants to add you as a ${if (asBig) "big" else "little"}!")
    }

    /**
     * Initiates the notification sending process by querying for the current user's name to place
     * in the string message
     */
    private fun sendAddNotification(sendingToBig: Boolean) {
        database.collection("users").document(auth.currentUser?.email!!).get().addOnCompleteListener{
            task -> constructAndUpdateNotification(sendingToBig, task)
        }
    }

    /**
     * Using the current user's name, constructs the notification message and updates the observed
     * user's notifications in the Firestore
     */
    private fun constructAndUpdateNotification(sendingToBig: Boolean, task: Task<DocumentSnapshot>) {
        val thisUserName = task.result!!.data?.get("displayName") as String
        ObservedUser.notifications.add("$thisUserName wants to add you as a ${if (sendingToBig) "big" else "little"}!")
        database.collection("users").document(ObservedUser.email!!).update("notifications", ObservedUser.notifications)
    }

    /**
     * Hides the add as little/big buttons if the current user is already a big/little for the
     * observed user
     */
    private fun filterButtons() {
        hideOrShowAddBigButton(ObservedUser.displayName)
        hideOrShowAddLittleButton(ObservedUser.displayName)
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
        ObservedUser = intent.getSerializableExtra("user_object") as User
        displayName.text = ObservedUser.displayName
        Glide.with(this).load(ObservedUser.profilePictureUri).into(profilePicture)
    }
}
