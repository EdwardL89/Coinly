package com.eightnineapps.coinly.views.fragments.profiles.big

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import kotlinx.android.synthetic.main.fragment_appeal.*

/**
 * Allows the little to send an appeal to an action done by the little's big
 */
class AppealFragment : Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_appeal, container, false)
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
        Glide.with(view!!).load(bigProfileViewModel.observedUserInstance.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
    }

    /**
     * Sets the on click listeners of all buttons of this activity
     */
    private fun setUpButtons() {
        cancel_appeal_coins_button.setOnClickListener {
            activity!!.onBackPressed()
        }
    }

}
