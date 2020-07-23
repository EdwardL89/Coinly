package com.eightnineapps.coinly.views.fragments.profiles.big

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import kotlinx.android.synthetic.main.fragment_big_profile.*

class BigProfileFragment: Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()
    private lateinit var observedUserInstance: User

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_big_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //viewModel.observedUserInstance = intent.getSerializableExtra("observed_user") as User
        //observedUserInstance = bigProfileViewModel.observedUserInstance
        //binding = DataBindingUtil.setContentView(this, R.layout.fragment_big_profile)
        //binding.observedUserInstance = observedUserInstance
        loadProfilePicture()
        setUpButtons()
    }

    /**
     * Determines actions based on what items in the action bar are selected
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            //onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfilePicture() {
        //Glide.with(view).load(observedUserInstance.profilePictureUri).into(view.findViewById(R.id.user_profile_picture))
    }

    /**
     * Sets up the on click listeners for all buttons
     */
    private fun setUpButtons() {
        appeal_button.setOnClickListener {
            findNavController().navigate(R.id.action_bigProfileFragment_to_appealFragment, null)
        }
        request_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_bigProfileFragment_to_requestCoinsFragment, null)
        }
        remove_big_button.setOnClickListener {
           //bigProfileViewModel.removeBigAndSendBack(this)
        }
    }


}