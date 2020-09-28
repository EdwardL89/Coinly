package com.eightnineapps.coinly.views.fragments.tablayout

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.adapters.NotificationsRecyclerViewAdapter
import com.eightnineapps.coinly.databinding.FragmentMyProfileBinding
import com.eightnineapps.coinly.viewmodels.fragmentviewmodels.MyProfileFragmentViewModel
import com.eightnineapps.coinly.views.activities.profiles.EditProfileActivity
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.view.*
import kotlin.math.roundToInt


class MyProfileFragment : Fragment() {

    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var myProfileFragmentViewModel: MyProfileFragmentViewModel
    private lateinit var fragmentView: View
    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        myProfileFragmentViewModel = ViewModelProvider(this).get(MyProfileFragmentViewModel::class.java)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        binding.myProfileViewModel = myProfileFragmentViewModel
        fragmentView = binding.root
        setUpEditProfileButton()
        loadProfilePicture()
        setupNotifications()
        setUpObservers()
        return fragmentView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats()
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
     * Attaches observers to the User live data to update the fragment UI
     */
    private fun setUpObservers() {
        val currentUserInstance = myProfileFragmentViewModel.currentUser

        currentUserInstance.bio.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.bioTextView.text = it
        })

        currentUserInstance.coins.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.coinCount.text = it.toString()
        })

        currentUserInstance.displayName.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.myDisplayNameTextView.text = it
        })

        currentUserInstance.numberOfBigs.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.bigsCount.text = it.toString()
        })

        currentUserInstance.numberOfLittles.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.littlesCount.text = it.toString()
        })

        currentUserInstance.profilePictureUri.observe(viewLifecycleOwner, Observer {
            if (it != null) loadProfilePicture()
        })
    }

    /**
     * Sets the adapter and layout manager for the notifications recycler view
     */
    private fun setupNotifications() {
        myProfileFragmentViewModel.updateNotifications(
            fragmentView.findViewById(R.id.notificationsRecyclerView),
            context
        )
    }

    /**
     * Sets the onClick listener for the edit profile button
     */
    private fun setUpEditProfileButton() {
        fragmentView.edit_profile_button.setOnClickListener {
            startActivity(
                Intent(
                    activity,
                    EditProfileActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
    }

    /**
     * Loads the profile picture
     */
    private fun loadProfilePicture() {
        Glide.with(activity!!).load(myProfileFragmentViewModel.currentUser.profilePictureUri.value).into(
            fragmentView.findViewById(
                R.id.my_profile_picture
            )
        )
    }

    /**
     * Loads the statistics of the current user through a count-up animation
     */
    private fun loadStats() {
        animateValue(my_profile_prizes_given_value, myProfileFragmentViewModel.currentUser.instance!!.numOfPrizesGiven)
        animateValue(my_profile_prizes_claimed_value, myProfileFragmentViewModel.currentUser.instance!!.numOfPrizesClaimed)
        animateValue(my_profile_average_price_of_prizes_given_value, myProfileFragmentViewModel.currentUser.instance!!.avgPriceOfPrizesGiven)
        animateValue(my_profile_average_price_of_prizes_claimed_value, myProfileFragmentViewModel.currentUser.instance!!.avgPriceOfPrizesClaimed)
    }

    /**
     * Implements the count-up animation for the given text view to the given end value
     */
    private fun animateValue(textView: TextView?, end: Int) {
        val animator = ValueAnimator()
        animator.setObjectValues(0, end)
        animator.addUpdateListener { animation -> textView?.text = animation.animatedValue.toString() }
        animator.setEvaluator { fraction, startValue, endValue -> (startValue as Int + fraction * (endValue as Int - startValue)).roundToInt() }
        animator.duration = 2000
        animator.start()
    }
}