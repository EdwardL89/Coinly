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
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_big_profile.*
import kotlinx.android.synthetic.main.fragment_big_profile.view.*

class BigProfileFragment: Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bigProfileViewModel.observedUserInstance = activity!!.intent.getSerializableExtra("observed_user") as User
        bigProfileViewModel.startQueryForPrizesSet()
        bigProfileViewModel.startQueryForClaimedPrizes()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_big_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadProfile()
        setUpButtons()
        addPrizesSetToRecycler(view)
        addPrizesClaimedToRecycler(view)
    }

    private fun addPrizesSetToRecycler(view: View) {
        if (bigProfileViewModel.hasLoadedPrizesSet()) {
            attachPrizesSetAdapter(view)
        } else {
            val prizesSetQueryTask = bigProfileViewModel.getPrizesSetQuery()!!
            if (prizesSetQueryTask.isComplete) {
                handlePrizesSetQueryTask(prizesSetQueryTask, view)
            } else {
                prizesSetQueryTask.addOnCompleteListener {
                    handlePrizesSetQueryTask(prizesSetQueryTask, view)
                }
            }
        }
    }

    private fun addPrizesClaimedToRecycler(view: View) {
        if (bigProfileViewModel.hasLoadedPrizesClaimed()) {
            attachPrizesClaimedAdapter(view)
        } else {
            val prizesClaimedQueryTask = bigProfileViewModel.getPrizesClaimedQuery()!!
            if (prizesClaimedQueryTask.isComplete) {
                handlePrizesClaimedQueryTask(prizesClaimedQueryTask, view)
            } else {
                prizesClaimedQueryTask.addOnCompleteListener {
                    handlePrizesClaimedQueryTask(prizesClaimedQueryTask, view)
                }
            }
        }
    }

    private fun handlePrizesSetQueryTask(prizesSetQueryTask: Task<QuerySnapshot>, view: View) {
        bigProfileViewModel.compilePrizesSet(prizesSetQueryTask.result!!)
        bigProfileViewModel.createPrizesSetAdapter()
        attachPrizesSetAdapter(view)
    }

    private fun handlePrizesClaimedQueryTask(prizesClaimedQueryTask: Task<QuerySnapshot>, view: View) {
        bigProfileViewModel.compilePrizesClaimed(prizesClaimedQueryTask.result!!)
        bigProfileViewModel.createPrizesClaimedAdapter()
        attachPrizesClaimedAdapter(view)
    }

    private fun attachPrizesSetAdapter(view: View) {
        view.prizesToClaimRecyclerView.adapter = bigProfileViewModel.getPrizesSetAdapter()
        if (bigProfileViewModel.getPrizesSetAdapter().itemCount == 0) {
            view.no_prizes_set_by_big_image.visibility = View.VISIBLE
        } else {
            view.no_prizes_set_by_big_image.visibility = View.INVISIBLE
        }
    }

    private fun attachPrizesClaimedAdapter(view: View) {
        view.prizesYouveClaimedRecyclerView.adapter = bigProfileViewModel.getPrizesClaimedAdapter()
        if (bigProfileViewModel.getPrizesClaimedAdapter().itemCount == 0) {
            view.no_prizes_claimed_image.visibility = View.VISIBLE
        } else {
            view.no_prizes_claimed_image.visibility = View.INVISIBLE
        }
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
           bigProfileViewModel.removeBigAndSendBack(context!!)
        }
    }


}