package com.eightnineapps.coinly.views.fragments.prizes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.LittleProfileViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_prizes_claimed_from_you.view.*

class PrizesClaimedFromYouFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel.startQueryForPrizesClaimed()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prizes_claimed_from_you, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPrizesClaimedFromYouToRecycler(view)
    }

    /**
     * Makes sure the query task is completed before continuing
     */
    private fun addPrizesClaimedFromYouToRecycler(view: View) {
        if (littleProfileViewModel.hasLoadedPrizesClaimed()) {
            attachPrizesClaimedAdapter(view)
        } else {
            val prizesClaimedQueryTask = littleProfileViewModel.getPrizesClaimedQuery()!!
            if (prizesClaimedQueryTask.isComplete) {
                handlePrizesClaimedQueryTask(prizesClaimedQueryTask, view)
            } else {
                prizesClaimedQueryTask.addOnCompleteListener {
                    handlePrizesClaimedQueryTask(prizesClaimedQueryTask, view)
                }
            }
        }
    }

    /**
     * Attaches the adapter to the prizes claimed recycler view and updates UI if it's empty
     */
    private fun attachPrizesClaimedAdapter(view: View) {
        view.prizesClaimedFromYouRecyclerView.adapter = littleProfileViewModel.getPrizesClaimedAdapter()
        if (littleProfileViewModel.getPrizesClaimedAdapter().itemCount == 0) {
            view.no_prizes_claimed_from_you_image.visibility = View.VISIBLE
        } else {
            view.no_prizes_claimed_from_you_image.visibility = View.INVISIBLE
        }
    }

    /**
     * Gathers all the prizes claimed and sets up the recyclerview to place them in
     */
    private fun handlePrizesClaimedQueryTask(prizesClaimedQueryTask: Task<QuerySnapshot>, view: View) {
        littleProfileViewModel.compilePrizesClaimed(prizesClaimedQueryTask.result!!)
        littleProfileViewModel.createPrizesClaimedAdapter(view)
        attachPrizesClaimedAdapter(view)
    }
}