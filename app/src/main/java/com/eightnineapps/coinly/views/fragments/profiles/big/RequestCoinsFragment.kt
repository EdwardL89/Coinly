package com.eightnineapps.coinly.views.fragments.profiles.big

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.Notification
import com.eightnineapps.coinly.enums.NotificationType
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import kotlinx.android.synthetic.main.fragment_request.*
import kotlin.random.Random

/**
 * Allows a little send a request to a big for coins
 */
class RequestCoinsFragment : Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfilePicture()
        setUpButtons()
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfilePicture() {
        val observedUser = bigProfileViewModel.observedUserInstance
        Glide.with(view!!).load(observedUser.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
        display_name_text_view.text = observedUser.displayName
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        send_request_button.setOnClickListener {
            if (hasEnteredCoinsAndReason()) {
                if (coinsIsPositive()) {
                    if (hasEnoughCoins()) {
                        val notification = constructRequestNotification(Integer.parseInt(coins_requesting_edit_text.text.toString()), request_reason_edit_text.text.toString())
                        Firestore.addNotification(bigProfileViewModel.observedUserInstance.email!!, notification)
                        Toast.makeText(context, "Request sent!", Toast.LENGTH_SHORT).show()
                        hideSoftKeyboard()
                        activity!!.onBackPressed()
                    } else {
                        Toast.makeText(context, "${bigProfileViewModel.observedUserInstance.displayName} doesn't have that many coins!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Coins must be positive", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Missing information!", Toast.LENGTH_SHORT).show()
            }
        }
        cancel_request_coins_button.setOnClickListener {
            hideSoftKeyboard()
            activity!!.onBackPressed()
        }
    }

    /**
     * Hides the keyboard from the screen
     */
    private fun hideSoftKeyboard() {
        val view = activity!!.currentFocus
        view?.let { v ->
            val imm = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    /**
     * Determines whether Big has enough coins to fulfill this request.
     */
    private fun hasEnoughCoins(): Boolean {
        return bigProfileViewModel.observedUserInstance.coins >= Integer.parseInt(coins_requesting_edit_text.text.toString())
    }

    /**
     * Determines whether the number of coins entered is positive
     */
    private fun coinsIsPositive(): Boolean {
        return Integer.parseInt(coins_requesting_edit_text.text.toString()) > 0
    }

    /**
     * Determines whether or not the user has entered a number for the coins requesting box and a reason
     */
    private fun hasEnteredCoinsAndReason(): Boolean {
        return coins_requesting_edit_text.text.toString() != "" && request_reason_edit_text.text.toString() != ""
    }

    /**
     * Constructs and returns a notification that holds information about a coin request
     */
    private fun constructRequestNotification(coinsRequesting: Int, reasonForRequest: String): Notification {
        val notification = Notification()
        notification.id = generateId()
        val myDisplayName = CurrentUser.instance!!.displayName
        notification.coins = coinsRequesting
        notification.moreInformation = reasonForRequest
        notification.type = NotificationType.REQUESTING_COINS
        notification.message = "$myDisplayName requested $coinsRequesting coins"
        notification.moreInformation = "$myDisplayName is requesting $coinsRequesting coins. \n\n Reason:\n$reasonForRequest"
        notification.profilePictureUri = CurrentUser.instance!!.profilePictureUri
        notification.addingToUserEmail = CurrentUser.instance!!.email!!
        return notification
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }
}
