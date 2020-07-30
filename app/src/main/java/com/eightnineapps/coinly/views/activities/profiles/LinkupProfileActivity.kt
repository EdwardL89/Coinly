package com.eightnineapps.coinly.views.activities.profiles

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.databinding.ActivityLinkupProfileBinding
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.enums.NotificationType.ADDING_AS_BIG
import com.eightnineapps.coinly.enums.NotificationType.ADDING_AS_LITTLE
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LinkupProfileViewModel
import kotlinx.android.synthetic.main.activity_linkup_profile.*

/**
 * Represents the profile of a single user of the app the current user is observing
 */
class LinkupProfileActivity : AppCompatActivity() {

    private lateinit var linkupProfileViewModel: LinkupProfileViewModel
    private lateinit var binding: ActivityLinkupProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        linkupProfileViewModel = ViewModelProvider(this).get(LinkupProfileViewModel::class.java)
        linkupProfileViewModel.observedUserInstance = intent.getSerializableExtra("observed_user") as User
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_linkup_profile)
        binding.linkupProfileViewModel = linkupProfileViewModel
        addCoinlyActionBarTitle()
        addBackArrowToActionBar()
        loadProfilePictureAndPrizesGiven()
        setupButtons()
    }

    /**
     * Makes for a clean transition back to the previous activity with no animation or flashes
     */
    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    /**
     * Determines actions based on what items in the action bar are selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Set the text on the add-as buttons
     */
    private fun setupButtons() {
        linkupProfileViewModel.getUpdatedObservedUser(linkupProfileViewModel.observedUserInstance).addOnCompleteListener {
            if (it.isSuccessful) {
                linkupProfileViewModel.observedUserInstance = it.result?.toObject(User::class.java)!!
                setUpAddAsButtons(true)
                setUpAddAsButtons(false)
                //hideOrShowPrizesGiven()
                //hideOrShowPrizesClaimed()
            }
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
        actionBar.customView = LayoutInflater.from(this).inflate(R.layout.app_bar_title, null)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#ffffff")))
    }

    /**
     * Sets up the add as buttons based on the status between the current and observed user
     */
    private fun setUpAddAsButtons(asBig: Boolean) {
        /*val addStatus = linkupProfileViewModel.getAddStatus(asBig)
        if (!addStatus.first && !addStatus.second && !addStatus.third)
            (if (asBig) add_as_big_button else add_as_little_button).setOnClickListener {
                linkupProfileViewModel.sendAddNotification(asBig)
                showRequested(if (asBig) add_as_big_button else add_as_little_button)
            }
        else if (addStatus.first) showRequested(if (asBig) add_as_big_button else add_as_little_button)
        else if (addStatus.second) showAdded(asBig, if (asBig) add_as_big_button else add_as_little_button)
        else updateButtonsForPendingRequests(if (asBig) ADDING_AS_LITTLE else ADDING_AS_BIG)*/
    }

    /**
     * Update the text on the buttons if there's already a pending request
     */
    private fun updateButtonsForPendingRequests(type: NotificationType) {
        val pendingRequestPair = linkupProfileViewModel.checkForPendingRequest(type)
        if (pendingRequestPair.second) setUpAddAsLittleAsAcceptRequest(pendingRequestPair.first)
        else setUpAddAsBigAsAcceptRequest(pendingRequestPair.first)
    }

    /**
     * Populates the visible UI elements of this activity to their respective data for the user
     */
    private fun loadProfilePictureAndPrizesGiven() {
        Glide.with(this).load(linkupProfileViewModel.observedUserInstance.profilePictureUri).into(user_profile_picture)
        displayPrizesGiven()
    }

    /**
     * Disables the add button and displays a message to notify that the observed user has already
     * been added
     */
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
     * Updates the add-as-little button to show an accept-request message since there's already a pending notification
     */
    private fun setUpAddAsLittleAsAcceptRequest(notification: Notification) {
        add_as_little_button.text = getString(R.string.accept_request)
        add_as_little_button.isEnabled = true
        add_as_little_button.setOnClickListener {
            linkupProfileViewModel.executeAndUpdateNotification(notification)
            add_as_little_button.text = getString(R.string.added_as_little)
            add_as_little_button.isEnabled = false
        }
    }

    /**
     * Updates the add-as-big button to show an accept-request message since there's already a pending notification
     */
    private fun setUpAddAsBigAsAcceptRequest(notification: Notification) {
        add_as_big_button.text = getString(R.string.accept_request)
        add_as_big_button.isEnabled = true
        add_as_big_button.setOnClickListener {
            linkupProfileViewModel.executeAndUpdateNotification(notification)
            add_as_big_button.text = getString(R.string.added_as_big)
            add_as_big_button.isEnabled = false
        }
    }

    /**
     * Determines whether we need to hide the prizes given info of the observed user
     */
    private fun hideOrShowPrizesGiven() {
        /*if (add_as_big_button.text == "Added as big") {
            prizes_given_lock.visibility = View.INVISIBLE
            prizesGivenRecyclerView.visibility = View.VISIBLE
            if (linkupProfileViewModel.observedUserInstance.prizesGiven.size == 0) no_prizes_given_image.visibility = View.VISIBLE
        } else {
            prizes_given_lock.visibility = View.VISIBLE
            no_prizes_given_image.visibility = View.INVISIBLE
            prizesGivenRecyclerView.visibility = View.INVISIBLE
        }*/
    }

    /**
     * Determines whether we need to hide the prizes claimed info of the observed user
     */
    private fun hideOrShowPrizesClaimed() {
        /*if (add_as_little_button.text == "Added as little") {
            prizes_claimed_lock.visibility = View.INVISIBLE
            prizesClaimedRecyclerView.visibility = View.VISIBLE
            if (linkupProfileViewModel.observedUserInstance.prizesClaimed.size == 0) no_prizes_claimed_image.visibility = View.VISIBLE
        } else {
            prizes_claimed_lock.visibility = View.VISIBLE
            no_prizes_claimed_image.visibility = View.INVISIBLE
            prizesClaimedRecyclerView.visibility = View.INVISIBLE
        }*/
    }

    /**
     * Load all the prizes given by this user if the recycler view is visible
     */
    private fun displayPrizesGiven() {
        if (prizesGivenRecyclerView.visibility == View.VISIBLE) linkupProfileViewModel.prizeLoader.loadAllPrizesGiven(prizesGivenRecyclerView)
        if (prizesClaimedRecyclerView.visibility == View.VISIBLE) linkupProfileViewModel.prizeLoader.loadAllPrizesClaimed(prizesClaimedRecyclerView)
    }

    /**
     * Adds a back arrow to navigate back to the previous activity
     */
    private fun addBackArrowToActionBar() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }
}
