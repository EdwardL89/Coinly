package com.eightnineapps.coinly.views.fragments.profiles.big

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.adapters.BigProfileAdapter
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import com.eightnineapps.coinly.views.activities.startup.HomeActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.fragment_big_profile.*

class BigProfileFragment: Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu and starts the
     * queries
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bigProfileViewModel.observedUserInstance = requireActivity().intent.getSerializableExtra("observed_user") as User
    }

    /**
     * Inflates the big's profile
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_big_profile, container, false)
    }

    /**
     * Begin loading the big's profile information
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        constructTabLayout()
        setUpButtons()
    }

    private fun constructTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("Prizes"))
        tab_layout.addTab(tab_layout.newTab().setText("Claimed"))
        tab_layout.tabGravity = TabLayout.GRAVITY_FILL
        view_pager.adapter = BigProfileAdapter(this)
        TabLayoutMediator(tab_layout, view_pager) { tab, pos ->
            if (pos == 0) tab.text = "Prizes"
            else tab.text = "Claimed"
        }.attach()

    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfile() {
        val observedUser = bigProfileViewModel.observedUserInstance
        Glide.with(requireView()).load(observedUser.profilePictureUri).into(requireView().findViewById(R.id.user_profile_picture))
        my_display_name_textView.text = observedUser.displayName
        bio_text_view.text = observedUser.bio
        coin_count.text = observedUser.coins.toString()
        bigs_count.text = observedUser.numOfBigs.toString()
        littles_count.text = observedUser.numOfLittles.toString()
        spending_power.text = CurrentUser.coins.value.toString()
    }

    /**
     * Sets up the on click listeners for all buttons
     */
    private fun setUpButtons() {
        request_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_bigProfileFragment_to_requestCoinsFragment, null)
        }
        remove_big_button.setOnClickListener {
            bigProfileViewModel.removeBigAndSendBack()
            Toast.makeText(context, "Removed ${bigProfileViewModel.observedUserInstance.displayName} as a big",
                Toast.LENGTH_SHORT).show()
            (context as Activity).finish()
            redrawAllBigsPage()
        }
    }

    /**
     * Refreshes the all big's fragment by re-selecting it
     */
    private fun redrawAllBigsPage() {
        HomeActivity.tabLayout.getTabAt(1)!!.select()
        HomeActivity.tabLayout.getTabAt(0)!!.select()
    }
}