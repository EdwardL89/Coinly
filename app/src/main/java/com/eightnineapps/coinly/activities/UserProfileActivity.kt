package com.eightnineapps.coinly.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.Notification
import com.eightnineapps.coinly.enums.NotificationType.ADDING_AS_BIG
import com.eightnineapps.coinly.enums.NotificationType.ADDING_AS_LITTLE
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.fragments.HomeFragments.Companion.currentUserSnapshot
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot

/**
 * Represents a single user of the app
 */
class UserProfileActivity : AppCompatActivity() {

    private lateinit var displayName: TextView
    private lateinit var addAsBigButton: Button
    private lateinit var addAsLittleButton: Button
    private lateinit var profilePicture: ImageView
    private lateinit var ObservedUser: User
    private lateinit var currentUser: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        instantiateUIElements()
        populateUIElements()
    }

    /**
     * Enables or disables the add-as buttons if not applicable. Sets the onClick listener otherwise
     */
    override fun onStart() {
        setupButtons()
        super.onStart()
    }

    /**
     * Set the onClick listeners for the "Add as" button
     */
    private fun setupButtons() {
        database.collection("users").document(ObservedUser.email!!).get().addOnCompleteListener{
                observedUserTask ->
                database.collection("users").document(currentUser.email!!).get().addOnCompleteListener {
                    currentUserTask ->
                    if (observedUserTask.isSuccessful && currentUserTask.isSuccessful ) {
                        val mostUpdatedObservedUser = observedUserTask.result?.toObject(User::class.java)!!
                        val mostUpdatedCurrentUser = currentUserTask.result?.toObject(User::class.java)!!
                        setUpAddAsBig(mostUpdatedObservedUser, mostUpdatedCurrentUser)
                        setUpAddAsLittle(mostUpdatedObservedUser, mostUpdatedCurrentUser)
                    }
                }
        }
    }

    /**
     * Sets up the "add as little" button and determines whether it should be enabled
     */
    private fun setUpAddAsLittle(observedUser: User, currentUser: User) {
        if (!alreadyRequested(false, observedUser) && !alreadyAdded(false, observedUser)) {
            addAsLittleButton.setOnClickListener {
                sendAddNotification(false, observedUser)
                addAsLittleButton.isEnabled = false
            }
        } else {
            addAsLittleButton.isEnabled = false
        }
    }

    /**
     * Sets up the "add as big" button and determines whether it should be enabled
     */
    private fun setUpAddAsBig(observedUser: User, currentUser: User) {
        val alreadyRequested = alreadyRequested(true, observedUser)
        val alreadyAdded = alreadyAdded(true, observedUser)
        val alreadyReceivedRequest = alreadyReceivedRequest(true, currentUser)
        if (!alreadyRequested && !alreadyAdded && !alreadyReceivedRequest) {
            Log.w("INFO", "THE WRONG BRANCH!")
            addAsBigButton.setOnClickListener {
                sendAddNotification(true, observedUser)
                addAsBigButton.text = getString(R.string.requested)
                addAsBigButton.isEnabled = false
            }
        } else if (alreadyRequested) { //YOU requested THEM
            addAsBigButton.text = getString(R.string.requested)
            addAsBigButton.isEnabled = false
        } else if (alreadyAdded) {
            addAsBigButton.text = getString(R.string.added_as_big)
            addAsBigButton.isEnabled = false
        } else { //THEY requested YOU. alreadyReceivedRequest is true
            Log.w("INFO", "THE CORRECT BRANCH!")
            queryForPendingRequest(observedUser, ADDING_AS_LITTLE)
        }
    }

    private fun queryForPendingRequest(observedUser: User, type: NotificationType) {
        database.collection("users").document(currentUser.email!!).get().addOnCompleteListener {
            updatedCurrentUserTask -> checkForPendingRequest(updatedCurrentUserTask, observedUser, type)
        }
    }

    private fun checkForPendingRequest(updatedCurrentUserTask: Task<DocumentSnapshot>, observedUser: User, type: NotificationType) {
        if (updatedCurrentUserTask.isSuccessful) {
            val updatedCurrentUser = updatedCurrentUserTask.result!!.toObject(User::class.java)!!
            val potentialNotification = updatedCurrentUser.notifications.find { it.type == type &&
                    it.toAddUserEmail == updatedCurrentUser.email &&
                    it.addingToUserEmail == observedUser.email } // should return just one
            if (potentialNotification != null) {
                addAsBigButton.text = getString(R.string.accept_request)
                addAsBigButton.isEnabled = true
                addAsBigButton.setOnClickListener {
                    potentialNotification.execute()
                    updatedCurrentUser.notifications.remove(potentialNotification)
                    //updaate
                    addAsBigButton.text = getString(R.string.added_as_big)
                    addAsBigButton.isEnabled = false
                }
            } else { // This else branch should never execute because all other scenarios are covered by the if else blocks in the setUpAs method
                addAsBigButton.isEnabled = false
            }
        }
    }

    private fun alreadyReceivedRequest(asBig: Boolean, mostUpdatedCurrentUser: User): Boolean {
        return if (asBig) {
            mostUpdatedCurrentUser.notifications.find { it.type == ADDING_AS_LITTLE &&
                    it.toAddUserEmail == currentUser.email &&
                    it.addingToUserEmail == ObservedUser.email } != null
        } else {
            mostUpdatedCurrentUser.notifications.find { it.type == ADDING_AS_BIG &&
                    it.toAddUserEmail == currentUser.email &&
                    it.addingToUserEmail == ObservedUser.email } != null
        }
    }

    /**
     * Checks to see if the two users have already been added to eachother
     */
    private fun alreadyAdded(asBig: Boolean, mostUpdatedObservedUser: User): Boolean {
        return if (asBig) {
            mostUpdatedObservedUser.littles.contains(auth.currentUser?.email!!)
        } else {
            mostUpdatedObservedUser.bigs.contains(auth.currentUser?.email!!)
        }
    }

    /**
     * Determines if the current user as already request the observed user to be added as a big/little
     */
    private fun alreadyRequested(asBig: Boolean, mostUpdatedObservedUser: User): Boolean {
        val notificationMessageTemplate = "${currentUser.displayName} wants to add you as a ${if (asBig) "big" else "little"}!"
        return mostUpdatedObservedUser.notifications.map{ it.message }.contains(notificationMessageTemplate)
    }

    /**
     * Initiates the notification sending process by querying for the current user's name to place
     * in the string message
     */
    private fun sendAddNotification(sendingToBig: Boolean, observedUser: User) {
        val newNotification = constructNotification(sendingToBig, observedUser)
        observedUser.notifications.add(newNotification)
        database.collection("users").document(observedUser.email!!).update("notifications", observedUser.notifications)
    }

    /**
     * Creates and returns a new notification object with its fields instantiated
     */
    private fun constructNotification(sendingToBig: Boolean, observedUser: User): Notification {
        val newNotification = Notification()
        newNotification.type = if (sendingToBig) ADDING_AS_BIG else ADDING_AS_LITTLE
        newNotification.addingToUserEmail = currentUser.email!!
        newNotification.toAddUserEmail = observedUser.email!!
        newNotification.message = "${currentUser.displayName} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        return newNotification
    }

    /**
     * Instantiates the UI elements to their resource layout IDs
     */
    private fun instantiateUIElements() {
        addAsBigButton = findViewById(R.id.add_as_big_button)
        profilePicture = findViewById(R.id.user_profile_picture)
        addAsLittleButton = findViewById(R.id.add_as_little_button)
        displayName = findViewById(R.id.profile_page_display_name_textView)
        currentUser = currentUserSnapshot.toObject(User::class.java)!!
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
