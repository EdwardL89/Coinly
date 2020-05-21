package com.eightnineapps.coinly.views.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.databinding.FragmentMyProfileBinding
import com.eightnineapps.coinly.viewmodels.MyProfileViewModel
import com.eightnineapps.coinly.views.activities.EditProfileActivity
import kotlinx.android.synthetic.main.fragment_my_profile.view.*

class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var myProfileViewModel: MyProfileViewModel

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        myProfileViewModel = ViewModelProvider(this).get(MyProfileViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        binding.myProfileViewModel = myProfileViewModel
        val view = binding.root
        displayEmptyRecyclerViewImages(view)
        setUpEditProfileButton(view)
        loadProfilePicture(view)
        setupNotifications(view)
        return view
    }

    /**
     * Override the onCreateOptionsMenu to inflate it with our custom layout
     */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_fragments_app_bar_menu, menu)
        menu.findItem(R.id.menu_search).isVisible = false
        super.onCreateOptionsMenu(menu, inflater)
    }

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    /**
     * Populates the current User's profile activity tab
     */
    private fun displayEmptyRecyclerViewImages(view: View) {
        val emptyPrizesImage = view.findViewById<ImageView>(R.id.no_prizes_image)
        val emptyNotificationsImage = view.findViewById<ImageView>(R.id.no_notifications_image)
        if (myProfileViewModel.getPrizesClaimed().value!!.isEmpty()) emptyPrizesImage.visibility = View.VISIBLE else emptyPrizesImage.visibility = View.INVISIBLE
        if (myProfileViewModel.getNotifications().value!!.isEmpty()) emptyNotificationsImage.visibility = View.VISIBLE else emptyNotificationsImage.visibility = View.INVISIBLE
    }

    /**
     * Sets the adapter and layout manager for the notifications recycler view
     */
    private fun setupNotifications(view: View) {
        myProfileViewModel.updateNotifications(view.findViewById(R.id.notificationsRecyclerView), context)
    }

    /**
     * Sets the onClick listener for the edit profile button
     */
    private fun setUpEditProfileButton(view: View) {
        view.edit_profile_button.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION))
        }
    }

    /**
     * Loads the profile picture
     */
    private fun loadProfilePicture(view: View) {
        Glide.with(activity!!).load(myProfileViewModel.getProfilePictureUri().value).into(view.findViewById(R.id.my_profile_picture))
    }
}