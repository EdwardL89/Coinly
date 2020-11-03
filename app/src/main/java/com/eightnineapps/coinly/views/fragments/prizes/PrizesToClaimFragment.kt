package com.eightnineapps.coinly.views.fragments.prizes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.eightnineapps.coinly.R
import com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles.BigProfileViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.android.synthetic.main.fragment_prizes_to_claim.view.*

class PrizesToClaimFragment: Fragment() {

    private val bigProfileViewModel: BigProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bigProfileViewModel.startQueryForPrizesSet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_prizes_to_claim, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPrizesSetToRecycler(view)
    }

    /**
     * Makes sure the query task is completed before continuing
     */
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

    /**
     * Attaches the adapter to the prizes set recycler view and updates UI if it's empty
     */
    private fun attachPrizesSetAdapter(view: View) {
        view.prizesToClaimRecyclerView.adapter = bigProfileViewModel.getPrizesSetAdapter()
        if (bigProfileViewModel.getPrizesSetAdapter().itemCount == 0) {
            view.no_prizes_set_by_big_image.visibility = View.VISIBLE
        } else {
            view.no_prizes_set_by_big_image.visibility = View.INVISIBLE
        }
    }

    /**
     * Gathers all the prizes set and sets up the recyclerview to place them in
     */
    private fun handlePrizesSetQueryTask(prizesSetQueryTask: Task<QuerySnapshot>, view: View) {
        bigProfileViewModel.compilePrizesSet(prizesSetQueryTask.result!!)
        bigProfileViewModel.createPrizesSetAdapter()
        attachPrizesSetAdapter(view)
    }

}