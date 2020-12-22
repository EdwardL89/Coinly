package com.eightnineapps.coinly.views.fragments.profiles.little

import android.app.Activity
import android.content.Context
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
import com.eightnineapps.coinly.adapters.LittleProfileAdapter
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import com.eightnineapps.coinly.views.activities.startup.HomeActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.dialog_confirm_removal.*
import kotlinx.android.synthetic.main.fragment_little_profile.*

class LittleProfileFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()
    private var savedContext: Context? = null

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu and starts the
     * queries
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel.observedUserInstance = requireActivity().intent.getSerializableExtra("observed_user") as User
    }

    /**
     * Inflates the little's profile
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_little_profile, container, false)
    }

    /**
     * Begin loading the littles's profile information
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        setUpButtons()
        constructTabLayout()
    }

    /**
     * Saves the context to be used in Toast messages in non-ui threads
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        savedContext = context
    }

    private fun constructTabLayout() {
        tab_layout.addTab(tab_layout.newTab().setText("Claimed From You"))
        tab_layout.addTab(tab_layout.newTab().setText("Your Set Prizes"))
        tab_layout.tabGravity = TabLayout.GRAVITY_FILL
        view_pager.adapter = LittleProfileAdapter(this)
        TabLayoutMediator(tab_layout, view_pager) { tab, pos ->
            if (pos == 0) tab.text = "Claimed From You"
            else tab.text = "Your Set Prizes"
        }.attach()
    }

    /**
     * Loads the observe user's profile picture
     */
    private fun loadProfile() {
        val observedUser = littleProfileViewModel.observedUserInstance
        Glide.with(requireView()).load(observedUser.profilePictureUri).into(requireView().findViewById(R.id.user_profile_picture))
        my_display_name_textView.text = observedUser.displayName
        bio_text_view.text = observedUser.bio
        coin_count.text = observedUser.coins.toString()
        bigs_count.text = observedUser.numOfBigs.toString()
        littles_count.text = observedUser.numOfLittles.toString()
    }

    /**
     * Attaches the on click listeners to all buttons
     */
    private fun setUpButtons() {
        give_coins_button.setOnClickListener {
            findNavController().navigate(R.id.action_littleProfileFragment_to_giveCoinsFragment, null)
        }
        remove_little_button.setOnClickListener {
            val confirmationDialog = littleProfileViewModel.openConfirmationDialog(requireContext())
            confirmationDialog.confirm_removal_button.setOnClickListener { removeLittle() }
            confirmationDialog.cancel_removal_button.setOnClickListener { confirmationDialog.cancel() }
        }
    }

    private fun removeLittle() {
        littleProfileViewModel.removeLittleAndSendBack()
        Toast.makeText(context, "Removed " +
                "${littleProfileViewModel.observedUserInstance.displayName} as a little", Toast.LENGTH_SHORT).show()
        (context as Activity).finish()
        redrawAllLittlesPage()
    }

    /**
     * Refreshes the all little's fragment by re-selecting it
     */
    private fun redrawAllLittlesPage() {
        HomeActivity.tabLayout.getTabAt(0)!!.select()
        HomeActivity.tabLayout.getTabAt(1)!!.select()
    }
}