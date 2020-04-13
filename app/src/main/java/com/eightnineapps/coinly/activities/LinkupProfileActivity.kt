package com.eightnineapps.coinly.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.activities.HomeActivity.Companion.database
import com.eightnineapps.coinly.activities.LoginActivity.Companion.auth
import com.eightnineapps.coinly.classes.Notification
import com.eightnineapps.coinly.enums.NotificationType.ADDING_AS_BIG
import com.eightnineapps.coinly.enums.NotificationType.ADDING_AS_LITTLE
import com.eightnineapps.coinly.classes.User
import com.eightnineapps.coinly.enums.NotificationType
import kotlinx.android.synthetic.main.activity_linkup_profile.*

/**
 * Represents a single user of the app
 */
class LinkupProfileActivity : AppCompatActivity() {

    private lateinit var currentUser: User
    private lateinit var observedUser: User
    private lateinit var displayName: TextView
    private lateinit var addAsBigButton: Button
    private lateinit var addAsLittleButton: Button
    private lateinit var profilePicture: ImageView
    private lateinit var prizesGivenLock: ImageView
    private lateinit var prizesClaimedLock: ImageView
    private lateinit var noPrizesGivenImage: ImageView
    private lateinit var noPrizesClaimedImage: ImageView
    private lateinit var allPrizesGivenRecyclerView: RecyclerView
    private lateinit var allPrizesClaimedRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_linkup_profile)
        instantiateUIElements()
        setupButtons()
        addCoinlyActionBarTitle()
        populateUIElements()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
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
     * Set the onClick listeners for the "Add as" button
     */
    private fun setupButtons() {
        database.collection("users").document(observedUser.email!!).get().addOnCompleteListener{
                observedUserTask ->
                database.collection("users").document(currentUser.email!!).get().addOnCompleteListener {
                    currentUserTask ->
                    if (observedUserTask.isSuccessful && currentUserTask.isSuccessful ) {
                        val mostUpdatedObservedUser = observedUserTask.result?.toObject(User::class.java)!!
                        val mostUpdatedCurrentUser = currentUserTask.result?.toObject(User::class.java)!!
                        setUpAddAsBig(mostUpdatedObservedUser, mostUpdatedCurrentUser)
                        setUpAddAsLittle(mostUpdatedObservedUser, mostUpdatedCurrentUser)
                        setUpPrizesGiven()
                        setUpPrizesClaimed()
                    }
                }
        }
    }

    /**
     * Sets up the "add as little" button and determines whether it should be enabled
     */
    private fun setUpAddAsLittle(observedUser: User, currentUser: User) {
        val alreadyRequested = alreadyRequested(false, observedUser)
        val alreadyAdded = alreadyAdded(false, observedUser)
        val alreadyReceivedRequest = alreadyReceivedRequest(false, currentUser)
        if (!alreadyRequested && !alreadyAdded && !alreadyReceivedRequest)
            addAsLittleButton.setOnClickListener {
                sendAddNotification(false, observedUser)
                showRequested(addAsLittleButton)
            }
        else if (alreadyRequested) showRequested(addAsLittleButton)
        else if (alreadyAdded) showAdded(false, addAsLittleButton)
        else checkForPendingRequest(currentUser, observedUser, ADDING_AS_BIG)
    }

    private fun showAdded(addedAsBig: Boolean, buttonToUpdate: Button) {
        buttonToUpdate.text = if (addedAsBig) "Added as big" else getString(R.string.added_as_little)
        buttonToUpdate.isEnabled = false
    }

    /**
     * Updates a button to show that it's function has already been performed, and disables it
     */
    private fun showRequested(buttonToUpdate: Button) {
        buttonToUpdate.text = getString(R.string.requested)
        buttonToUpdate.isEnabled = false
    }

    /**
     * Sets up the "add as big" button and determines whether it should be enabled
     */
    private fun setUpAddAsBig(observedUser: User, currentUser: User) {
        val alreadyRequested = alreadyRequested(true, observedUser)
        val alreadyAdded = alreadyAdded(true, observedUser)
        val alreadyReceivedRequest = alreadyReceivedRequest(true, currentUser)
        if (!alreadyRequested && !alreadyAdded && !alreadyReceivedRequest)
            addAsBigButton.setOnClickListener {
                sendAddNotification(true, observedUser)
                showRequested(addAsBigButton)
            }
        else if (alreadyRequested) showRequested(addAsBigButton)
        else if (alreadyAdded) showAdded(true, addAsBigButton)
        else checkForPendingRequest(currentUser, observedUser, ADDING_AS_LITTLE)
    }

    /**
     * Initiates the process of checking if the current user has a pending request
     */
    private fun checkForPendingRequest(currentUser: User, observedUser: User, type: NotificationType) {
        val notification = currentUser.notifications.find {
            it.type == type && it.toAddUserEmail == currentUser.email && it.addingToUserEmail == observedUser.email }!!
        if (type == ADDING_AS_BIG)  setUpAddAsLittleAsAcceptRequest(notification, currentUser) else setUpAddAsBigAsAcceptRequest(notification, currentUser)
    }

    /**
     * Updates the add-as-little button to show an accept-request message since there's already a pending notification
     */
    private fun setUpAddAsLittleAsAcceptRequest(notification: Notification, updatedCurrentUser: User) {
        addAsLittleButton.text = getString(R.string.accept_request)
        addAsLittleButton.isEnabled = true
        addAsLittleButton.setOnClickListener {
            executeAndUpdateNotification(notification, updatedCurrentUser)
            addAsLittleButton.text = getString(R.string.add_as_little)
            addAsLittleButton.isEnabled = false
        }
    }

    /**
     * Updates the add-as-big button to show an accept-request message since there's already a pending notification
     */
    private fun setUpAddAsBigAsAcceptRequest(notification: Notification, updatedCurrentUser: User) {
        addAsBigButton.text = getString(R.string.accept_request)
        addAsBigButton.isEnabled = true
        addAsBigButton.setOnClickListener {
            executeAndUpdateNotification(notification, updatedCurrentUser)
            addAsBigButton.text = getString(R.string.added_as_big)
            addAsBigButton.isEnabled = false
        }
    }

    /**
     * Executes the given notification and updates the notification list
     */
    private fun executeAndUpdateNotification(notification: Notification, user: User) {
        notification.execute()
        user.notifications.remove(notification)
        database.collection("users").document(user.email!!).update("notifications", user.notifications)

    }

    /**
     * Checks if the current user has already received the respective notification from the observed user
     */
    private fun alreadyReceivedRequest(asBig: Boolean, mostUpdatedCurrentUser: User): Boolean {
           return mostUpdatedCurrentUser.notifications.find {
                    it.type == (if (asBig) ADDING_AS_LITTLE else ADDING_AS_BIG)  &&
                    it.toAddUserEmail == currentUser.email &&
                    it.addingToUserEmail == observedUser.email } != null
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
        newNotification.profilePictureUri = currentUser.profilePictureUri
        newNotification.message = "${currentUser.displayName} wants to add you as a ${if (sendingToBig) "big" else "little"}!"
        return newNotification
    }

    /**
     * Determines whether we need to hide the prizes given info of the observed user
     */
    private fun setUpPrizesGiven() {
        if (addAsBigButton.text == "Added as big") {
            prizesGivenLock.visibility = View.INVISIBLE
            prizesGivenRecyclerView.visibility = View.VISIBLE
            if (observedUser.prizesGiven.size == 0) noPrizesGivenImage.visibility = View.VISIBLE
        } else {
            prizesGivenLock.visibility = View.VISIBLE
            noPrizesGivenImage.visibility = View.INVISIBLE
            prizesGivenRecyclerView.visibility = View.INVISIBLE
        }
    }

    /**
     * Determines whether we need to hide the prizes claimed info of the observed user
     */
    private fun setUpPrizesClaimed() {
        if (addAsLittleButton.text == "Added as little") {
            prizesClaimedLock.visibility = View.INVISIBLE
            prizesClaimedRecyclerView.visibility = View.VISIBLE
            if (observedUser.prizesClaimed.size == 0) noPrizesClaimedImage.visibility = View.VISIBLE
        } else {
            prizesClaimedLock.visibility = View.VISIBLE
            noPrizesClaimedImage.visibility = View.INVISIBLE
            prizesClaimedRecyclerView.visibility = View.INVISIBLE
        }
    }

    /**
     * Instantiates the UI elements to their resource layout IDs
     */
    private fun instantiateUIElements() {
        addAsBigButton = findViewById(R.id.add_as_big_button)
        profilePicture = findViewById(R.id.user_profile_picture)
        displayName = findViewById(R.id.my_display_name_textView)
        addAsLittleButton = findViewById(R.id.add_as_little_button)
        prizesGivenLock = findViewById(R.id.prizes_given_lock)
        prizesClaimedLock = findViewById(R.id.prizes_claimed_lock)
        noPrizesGivenImage = findViewById(R.id.no_prizes_given_image)
        noPrizesClaimedImage = findViewById(R.id.no_prizes_claimed_image)
        allPrizesGivenRecyclerView = findViewById(R.id.prizesGivenRecyclerView)
        allPrizesClaimedRecyclerView = findViewById(R.id.prizesClaimedRecyclerView)
        currentUser = intent.getSerializableExtra("current_user") as User
        observedUser = intent.getSerializableExtra("observed_user") as User
    }

    /**
     * Populates the visible UI elements of this activity to their respective data for the user
     */
    private fun populateUIElements() {
        displayName.text = observedUser.displayName
        Glide.with(this).load(observedUser.profilePictureUri).into(profilePicture)

    }
}
