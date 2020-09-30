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
import com.bumptech.glide.Glide
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.databinding.FragmentMyProfileBinding
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.viewmodels.fragmentviewmodels.MyProfileFragmentViewModel
import com.eightnineapps.coinly.views.activities.profiles.EditProfileActivity
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.view.*
import kotlin.math.roundToInt


class MyProfileFragment : Fragment() {

    private lateinit var fragmentView: View
    private lateinit var binding: FragmentMyProfileBinding
    private lateinit var myProfileFragmentViewModel: MyProfileFragmentViewModel

    /**
     * Overrides the onCreate method to allow the fragments to have an options menu
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
        myProfileFragmentViewModel = ViewModelProvider(this).get(MyProfileFragmentViewModel::class.java)
        myProfileFragmentViewModel.startQueryForNotifications()
    }

    /**
     * Inflates the my profile fragment
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_profile, container, false)
        binding.currentUser = CurrentUser
        fragmentView = binding.root
        setupNotifications(fragmentView)
        setUpEditProfileButton()
        loadProfilePicture()
        setUpObservers()
        return fragmentView
    }

    /**
     * Animate the stat values after the view is created
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        animateStats()
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
     * Attaches observers to the User live data to update the fragment UI
     */
    private fun setUpObservers() {
        CurrentUser.bio.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.bioTextView.text = it
        })
        CurrentUser.coins.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.coinCount.text = it.toString()
        })
        CurrentUser.displayName.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.myDisplayNameTextView.text = it
        })
        CurrentUser.numberOfBigs.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.bigsCount.text = it.toString()
        })
        CurrentUser.numberOfLittles.observe(viewLifecycleOwner, Observer {
            if (it != null) binding.littlesCount.text = it.toString()
        })
        CurrentUser.profilePictureUri.observe(viewLifecycleOwner, Observer {
            if (it != null) loadProfilePicture()
        })
    }

    /**
     * Sets the adapter and layout manager for the notifications recycler view
     */
    private fun setupNotifications(view: View) {
        if (myProfileFragmentViewModel.hasLoadedNotifications()) {
            attachAdapter(view)
        } else {
            val notificationsQueryTask = myProfileFragmentViewModel.getAllNotificationsQuery()!!
            if (notificationsQueryTask.isComplete) {
                handleQueryTask(notificationsQueryTask, view)
            } else {
                notificationsQueryTask.addOnCompleteListener {
                    handleQueryTask(notificationsQueryTask, view)
                }
            }
        }
    }

    /**
     * Gathers all the notifications and sets up the recyclerview to place them in
     */
    private fun handleQueryTask(notificationsQueryTask: Task<QuerySnapshot>, view: View) {
        myProfileFragmentViewModel.compileNotificationsToList(notificationsQueryTask.result!!)
        myProfileFragmentViewModel.createAdapter()
        attachAdapter(view)
    }

    /**
     * Attaches the recyclerview's adapter from when it was scrolled off screen
     */
    private fun attachAdapter(view: View) {
        view.notificationsRecyclerView.adapter = myProfileFragmentViewModel.getAdapter()
        if (myProfileFragmentViewModel.getAdapter().itemCount == 0) {
            view.no_notifications_image.visibility = View.VISIBLE
        } else {
            view.no_notifications_image.visibility = View.INVISIBLE
        }
    }

    /**
     * Sets the onClick listener for the edit profile button
     */
    private fun setUpEditProfileButton() {
        fragmentView.edit_profile_button.setOnClickListener {
            startActivity(Intent(activity, EditProfileActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
            )
        }
    }

    /**
     * Loads the profile picture
     */
    private fun loadProfilePicture() {
        Glide.with(activity!!).load(CurrentUser.profilePictureUri.value).into(
            fragmentView.findViewById(R.id.my_profile_picture)
        )
    }

    /**
     * Loads the statistics of the current user through a count-up animation
     */
    private fun animateStats() {
        animateValue(my_profile_prizes_given_value, CurrentUser.numOfPrizesGiven.value!!)
        animateValue(my_profile_prizes_claimed_value, CurrentUser.numOfPrizesClaimed.value!!)
        animateValue(my_profile_average_price_of_prizes_given_value, CurrentUser.avgPriceOfPrizesGiven.value!!)
        animateValue(my_profile_average_price_of_prizes_claimed_value, CurrentUser.avgPriceOfPrizesClaimed.value!!)
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