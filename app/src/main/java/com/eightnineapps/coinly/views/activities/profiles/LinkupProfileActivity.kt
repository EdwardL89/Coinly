package com.eightnineapps.coinly.views.activities.profiles

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
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
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LinkupProfileViewModel
import kotlinx.android.synthetic.main.activity_linkup_profile.*
import kotlin.math.roundToInt

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
        loadStats()
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
                linkupProfileViewModel.retrieveObservedUserBigs().addOnSuccessListener {
                    it2 -> for (doc in it2) {
                        linkupProfileViewModel.observedUserInstanceBigs.add(doc["email"].toString())
                    }
                    linkupProfileViewModel.retrieveObservedUserLittles().addOnSuccessListener {
                        it3 -> for (doc in it3) {
                            linkupProfileViewModel.observedUserInstanceLittles.add(doc["email"].toString())
                        }
                        setUpAddAsButtons(true)
                        setUpAddAsButtons(false)
                    }
                }
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
        Firestore.getNotifications(linkupProfileViewModel.observedUserInstance.email!!).get().addOnSuccessListener { it ->
            val notificationMessageTemplate = "${CurrentUser.instance!!.displayName} wants to add you as a ${if (asBig) "big" else "little"}!"
            val alreadyRequested = it.map{ queryDocumentSnapshot ->  queryDocumentSnapshot["message"] }.contains(notificationMessageTemplate)
            val alreadyAdded = linkupProfileViewModel.alreadyAdded(asBig)
            Firestore.getNotifications(CurrentUser.instance!!.email!!).get().addOnSuccessListener {
                val alreadyReceivedRequest = it.find { doc ->
                    doc["type"] == (if (asBig) ADDING_AS_LITTLE else ADDING_AS_BIG)  &&
                    doc["toAddUserEmail"] == CurrentUser.instance!!.email &&
                    doc["addingToUserEmail"] == CurrentUser.instance!!.email } != null

                val addStatus = Triple(alreadyRequested, alreadyAdded, alreadyReceivedRequest)

                if (!addStatus.first && !addStatus.second && !addStatus.third)
                    (if (asBig) add_as_big_button else add_as_little_button).setOnClickListener {
                        linkupProfileViewModel.sendAddNotification(asBig)
                        showRequested(if (asBig) add_as_big_button else add_as_little_button)
                    }
                else if (addStatus.first) showRequested(if (asBig) add_as_big_button else add_as_little_button)
                else if (addStatus.second) showAdded(asBig, if (asBig) add_as_big_button else add_as_little_button)
                else updateButtonsForPendingRequests(if (asBig) ADDING_AS_LITTLE else ADDING_AS_BIG)
            }
        }
    }

    /**
     * Update the text on the buttons if there's already a pending request
     */
    private fun updateButtonsForPendingRequests(type: NotificationType) {
        Firestore.getNotifications(CurrentUser.instance!!.email!!).get().addOnSuccessListener {
            val pendingNotification = it.find { queryDocumentSnapshot ->
                queryDocumentSnapshot["type"] == type &&
                        queryDocumentSnapshot["toAddUserEmail"] == CurrentUser.instance!!.email &&
                        queryDocumentSnapshot["addingToUserEmail"] == linkupProfileViewModel.observedUserInstance.email
            }!!.toObject(Notification::class.java)
            val pendingRequestPair = if (type == ADDING_AS_BIG) Pair(pendingNotification, true) else Pair(pendingNotification, false)
            if (pendingRequestPair.second) setUpAddAsLittleAsAcceptRequest(pendingRequestPair.first)
            else setUpAddAsBigAsAcceptRequest(pendingRequestPair.first)
        }
    }

    /**
     * Populates the visible UI elements of this activity to their respective data for the user
     */
    private fun loadProfilePictureAndPrizesGiven() {
        Glide.with(this).load(linkupProfileViewModel.observedUserInstance.profilePictureUri).into(user_profile_picture)
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
     * Adds a back arrow to navigate back to the previous activity
     */
    private fun addBackArrowToActionBar() {
        supportActionBar!!.setHomeAsUpIndicator(R.drawable.arrow_back)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    /**
     * Loads the statistics of the current user through a count-up animation
     */
    private fun loadStats() {
        animateValue(linkup_prizes_given_value, linkupProfileViewModel.observedUserInstance.numOfPrizesGiven)
        animateValue(linkup_prizes_claimed_value, linkupProfileViewModel.observedUserInstance.numOfPrizesClaimed)
        animateValue(linkup_average_price_of_prizes_given_value, linkupProfileViewModel.observedUserInstance.avgPriceOfPrizesGiven)
        animateValue(linkup_average_price_of_prizes_claimed_value, linkupProfileViewModel.observedUserInstance.avgPriceOfPrizesClaimed)
    }

    /**
     * Implements the count-up animation for the given text view to the given end value
     */
    private fun animateValue(textView: TextView?, end: Int) {
        val animator = ValueAnimator()
        animator.setObjectValues(0, end)
        animator.addUpdateListener { animation -> textView?.text = animation.animatedValue.toString() }
        animator.setEvaluator { fraction, startValue, endValue -> (startValue as Int + fraction * (endValue as Int - startValue)).roundToInt() }
        animator.duration = 2000
        animator.start()
    }
}
