package com.eightnineapps.coinly.views.fragments.profiles.big

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

/**
 * Allows a little send a request to a big for coins
 */
class RequestCoinsFragment : Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

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
                if (hasEnoughCoins()) {
                    val notification = constructRequestNotification(Integer.parseInt(coins_requesting_edit_text.text.toString()), request_reason_edit_text.text.toString())
                    bigProfileViewModel.observedUserInstance.notifications.add(notification)
                    Firestore.updateNotifications(bigProfileViewModel.observedUserInstance)
                } else {
                    Toast.makeText(context, "${bigProfileViewModel.observedUserInstance.displayName} doesn't have that many coins!", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Missing information!", Toast.LENGTH_SHORT).show()
            }
        }
        cancel_request_coins_button.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

    private fun hasEnoughCoins(): Boolean {
        return bigProfileViewModel.observedUserInstance.coins >= Integer.parseInt(coins_requesting_edit_text.text.toString())
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
        val myDisplayName = CurrentUser.instance!!.displayName
        notification.coins = coinsRequesting
        notification.moreInformation = reasonForRequest
        notification.type = NotificationType.REQUESTING_COINS
        notification.message = "$myDisplayName requested coins"
        notification.moreInformation = "$myDisplayName is requesting $coinsRequesting coins. \n\n Reason:\n$reasonForRequest"
        notification.profilePictureUri = CurrentUser.instance!!.profilePictureUri
        notification.addingToUserEmail = CurrentUser.instance!!.email!!
        return notification
    }
}
