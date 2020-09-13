package com.eightnineapps.coinly.views.fragments.profiles.big

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
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import kotlinx.android.synthetic.main.fragment_big_profile.*

class BigProfileFragment: Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_big_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bigProfileViewModel.observedUserInstance = activity!!.intent.getSerializableExtra("observed_user") as User
        loadProfile()
        setUpButtons()
        bigProfileViewModel.loadSetPrizes(prizesToClaimRecyclerView, context!!)
        bigProfileViewModel.loadClaimedPrizes(prizesYouveClaimedRecyclerView, context!!)
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfile() {
        val observedUser = bigProfileViewModel.observedUserInstance
        Glide.with(view!!).load(observedUser.profilePictureUri).into(view!!.findViewById(R.id.user_profile_picture))
        my_display_name_textView.text = observedUser.displayName
        bio_text_view.text = observedUser.bio
        coin_count.text = observedUser.coins.toString()
        bigs_count.text = observedUser.numOfBigs.toString()
        littles_count.text = observedUser.numOfLittles.toString()
    }

    /**
     * Sets up the on click listeners for all buttons
     */
    private fun setUpButtons() {
        request_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_bigProfileFragment_to_requestCoinsFragment, null)
        }
        remove_big_button.setOnClickListener {
           bigProfileViewModel.removeBigAndSendBack(context!!)
        }
    }


}