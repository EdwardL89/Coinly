package com.eightnineapps.coinly.views.fragments.profiles.little

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
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import kotlinx.android.synthetic.main.fragment_give_coins.*
import kotlin.random.Random

/**
 * Lets a big give a chosen number of coins to a little
 */
class GiveCoinsFragment : Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()
    private val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_give_coins, container, false)
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
        val observedUser = littleProfileViewModel.observedUserInstance
        Glide.with(view!!).load(observedUser.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
        display_name_text_view.text = observedUser.displayName
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            if (hasEnteredCoins()) {
                if (coinsIsPositive()) {
                    if (hasEnoughCoins(Integer.parseInt(coins_giving_edit_text.text.toString()))) {
                        val notification = constructNotification(Integer.parseInt(coins_giving_edit_text.text.toString()), optional_note_edit_text.text.toString())
                        notification.execute()
                        Firestore.addNotification(littleProfileViewModel.observedUserInstance.email!!, notification)
                        Toast.makeText(context, "Coins transferred!", Toast.LENGTH_SHORT).show()
                        hideSoftKeyboard()
                        activity!!.onBackPressed()
                    } else {
                        Toast.makeText(context, "You don't have that many coins!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Coins must be positive", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Enter a coin amount", Toast.LENGTH_SHORT).show()
            }
        }
        cancel_give_coins_button.setOnClickListener {
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
     * Determines whether the number of coins entered is positive
     */
    private fun coinsIsPositive(): Boolean {
        return Integer.parseInt(coins_giving_edit_text.text.toString()) > 0
    }

    /**
     * Constructs the notification to be sent to the little with all the information to inform them
     * of the coins they've been given
     */
    private fun constructNotification(coinsGiving: Int, optionalNote: String): Notification {
        val notification = Notification()
        notification.id = generateId()
        notification.coins = coinsGiving
        notification.moreInformation = optionalNote
        notification.type = NotificationType.GIVING_COINS
        notification.toAddUserEmail = littleProfileViewModel.observedUserInstance.email!!
        notification.profilePictureUri = CurrentUser.instance!!.profilePictureUri
        notification.message = "${CurrentUser.instance!!.displayName} gave you $coinsGiving coins"
        return notification
    }

    /**
     * Generates a random 30 character, alphanumerical id for each user
     */
    private fun generateId(): String {
        return (1..30).map { Random.nextInt(0, charPool.size) }.map(charPool::get).joinToString("")
    }

    /**
     * Determines whether or not the user has entered a number for the coins to give.
     */
    private fun hasEnteredCoins(): Boolean {
        return coins_giving_edit_text.text.toString() != ""
    }

    /**
     * Determines if the current user has enough coins to give.
     */
    private fun hasEnoughCoins(coinsGiving: Int): Boolean {
        return CurrentUser.instance!!.coins >= coinsGiving
    }
}
