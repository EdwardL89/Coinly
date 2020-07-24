package com.eightnineapps.coinly.views.fragments.profiles.little

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import kotlinx.android.synthetic.main.fragment_little_profile.*

class LittleProfileFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_little_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        littleProfileViewModel.observedUserInstance = activity!!.intent.getSerializableExtra("observed_user") as User
        loadProfile()
        setUpButtons()
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfile() {
        val observedUser = littleProfileViewModel.observedUserInstance
        Glide.with(view!!).load(observedUser.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
        my_display_name_textView.text = observedUser.displayName
        bio_text_view.text = observedUser.bio
        coin_count.text = observedUser.coins.toString()
        bigs_count.text = observedUser.bigs.size.toString()
        littles_count.text = observedUser.littles.size.toString()
    }

    /**
     * Attaches the on click listeners to all buttons
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_littleProfileFragment_to_giveCoinsFragment, null)
        }
        revoke_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_littleProfileFragment_to_revokeCoinsFragment, null)
        }
        remove_little_button.setOnClickListener {
            littleProfileViewModel.removeLittleAndSendBack(context!!)
        }
    }

}