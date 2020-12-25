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
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import kotlinx.android.synthetic.main.fragment_give_coins.*

/**
 * Lets a big give a chosen number of coins to a little
 */
class GiveCoinsFragment : Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()

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
        Glide.with(requireView()).load(observedUser.profilePictureUri).into(requireView().findViewById(R.id.user_profile_picture))
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
                        littleProfileViewModel
                            .sendAndExecuteGiveNotification(
                                Integer.parseInt(coins_giving_edit_text.text.toString()),
                                optional_note_edit_text.text.toString())
                            Toast.makeText(context, "Coins transferred!", Toast.LENGTH_SHORT).show()
                        hideKeyboardAndReturn()
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
            hideKeyboardAndReturn()
        }
    }

    /**
     * Hides soft input keyboard and returns to the previous fragment
     */
    private fun hideKeyboardAndReturn() {
        hideSoftKeyboard()
        requireActivity().onBackPressed()
    }

    /**
     * Hides the keyboard from the screen
     */
    private fun hideSoftKeyboard() {
        val view = requireActivity().currentFocus
        view?.let { v ->
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
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
