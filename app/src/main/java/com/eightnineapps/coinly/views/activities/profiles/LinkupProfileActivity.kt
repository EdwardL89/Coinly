package com.eightnineapps.coinly.views.activities.profiles

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.databinding.ActivityLinkupProfileBinding
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LinkupProfileViewModel
import com.google.firebase.firestore.DocumentSnapshot
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
        binding = DataBindingUtil.setContentView(this, R.layout.activity_linkup_profile)
        binding.linkupProfileViewModel = linkupProfileViewModel
        super.onCreate(savedInstanceState)
        setupProfilePage()
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
     * Sets up the required UI elements of the observed user's profile page
     */
    private fun setupProfilePage() {
        addCoinlyActionBarTitle()
        addBackArrowToActionBar()
        loadProfilePicture()
        loadStats()
    }

    /**
     * determines the statuses for the big and little buttons
     */
    private fun setupButtons() {
        determineAddAsBigButtonStatus()
        determineAddAsLittleButtonStatus()
    }

    /**
     * Sets up the "add as big" button to reflect the current relationship status
     * between the current and observed user
     */
    private fun determineAddAsBigButtonStatus() {
        linkupProfileViewModel.queryForContainedLittle().addOnCompleteListener {
            if (it.result!!.exists()) showAddedAsBig()
            else determineBigConnectionStatus()
        }
    }

    /**
     * Sets up the "add as little" button to reflect the current relationship status
     * between the current and observed user
     */
    private fun determineAddAsLittleButtonStatus() {
        linkupProfileViewModel.queryForContainedBig().addOnCompleteListener {
            if (it.result!!.exists()) showAddedAsLittle()
            else determineLittleConnectionStatus()
        }
    }

    /**
     * Determines the current connection status between the current and observed user
     * in regards to adding the observed user as a big
     */
    private fun determineBigConnectionStatus() {
        linkupProfileViewModel.queryForAlreadyRequestedBig().addOnCompleteListener {
            if (!it.result!!.isEmpty) showRequestedBig()
            else checkForReceivedRequestFromBig()
        }
    }

    /**
     * Determines the current connection status between the current and observed user
     * in regards to adding the observed user as a little
     */
    private fun determineLittleConnectionStatus() {
        linkupProfileViewModel.queryForAlreadyRequestedLittle().addOnCompleteListener {
            if (!it.result!!.isEmpty) showRequestedLittle()
            else checkForReceivedRequestFromLittle()
        }
    }

    /**
     * Checks if the current user has already received a request to be added as a big
     * by the observed user. If not, sets up the button as normal
     */
    private fun checkForReceivedRequestFromBig() {
        linkupProfileViewModel.queryForReceivedRequestFromBig().addOnCompleteListener {
            if (!it.result!!.isEmpty) showAcceptRequestFromBig(it.result!!.first())
             else setupStandardAddBigButton()
        }
    }

    /**
     * Checks if the current user has already received a request to be added as a little
     * by the observed user. If not, sets up the button as normal
     */
    private fun checkForReceivedRequestFromLittle() {
        linkupProfileViewModel.queryForReceivedRequestFromLittle().addOnCompleteListener {
            if (!it.result!!.isEmpty) showAcceptRequestFromLittle(it.result!!.first())
            else setupStandardAddLittleButton()
        }
    }

    /**
     * Sets up the procedure to send an "add as big" notification to the observed user
     */
    private fun setupStandardAddBigButton() {
        add_as_big_button.setOnClickListener {
            linkupProfileViewModel.sendAddBigNotification()
            showRequestedBig()
        }
    }

    /**
     * Sets up the procedure to send an "add as little" notification to the observed user
     */
    private fun setupStandardAddLittleButton() {
        add_as_little_button.setOnClickListener {
            linkupProfileViewModel.sendAddLittleNotification()
            showRequestedLittle()
        }
    }

    /**
     * Edits the "add as big" button to display that the observed user has already been
     * added as a big
     */
    private fun showAddedAsBig() {
        add_as_big_button.text = getString(R.string.added_as_big)
        add_as_big_button.isEnabled = false
    }

    /**
     * Edits the "add as little" button to display that the observed user has already been
     * added as a little
     */
    private fun showAddedAsLittle() {
        add_as_little_button.text = getString(R.string.added_as_little)
        add_as_little_button.isEnabled = false
    }

    /**
     * Updates the "add as big" button to show that it's function has already been performed,
     * and disables it
     */
    private fun showRequestedBig() {
        add_as_big_button.text = getString(R.string.requested)
        add_as_big_button.isEnabled = false
    }

    /**
     * Updates the "add as little" button to show that it's function has already been performed,
     * and disables it
     */
    private fun showRequestedLittle() {
        add_as_little_button.text = getString(R.string.requested)
        add_as_little_button.isEnabled = false
    }

    /**
     * Updates the "add as big" button to let the user accept the already received request by the
     * observed user to be added as a little
     */
    private fun showAcceptRequestFromBig(notificationSnapshot: DocumentSnapshot) {
        add_as_big_button.text = getString(R.string.accept_request)
        add_as_big_button.isEnabled = true
        add_as_big_button.setOnClickListener {
            linkupProfileViewModel.executeAndUpdateNotification(notificationSnapshot)
            showAddedAsBig()
        }
    }

    /**
     * Updates the "add as little" button to let the user accept the already received request by the
     * observed user to be added as a big
     */
    private fun showAcceptRequestFromLittle(notificationSnapshot: DocumentSnapshot) {
        add_as_little_button.text = getString(R.string.accept_request)
        add_as_little_button.isEnabled = true
        add_as_little_button.setOnClickListener {
            linkupProfileViewModel.executeAndUpdateNotification(notificationSnapshot)
            showAddedAsLittle()
        }
    }

    /**
     * Populates the visible UI elements of this activity to their respective data for the user
     */
    private fun loadProfilePicture() {
        Glide.with(this).load(linkupProfileViewModel.observedUserInstance.profilePictureUri).into(user_profile_picture)
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
}
