package com.eightnineapps.coinly.views.fragments.profiles.little

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
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
        Glide.with(view!!).load(observedUser.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
        display_name_text_view.text = observedUser.displayName
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_give_coins_button.setOnClickListener {
            activity!!.onBackPressed()
        }
    }
}
