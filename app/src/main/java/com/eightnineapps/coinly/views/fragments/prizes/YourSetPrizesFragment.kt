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
import kotlinx.android.synthetic.main.fragment_your_set_prizes.view.*

class YourSetPrizesFragment: Fragment() {

    private val littleProfileViewModel: LittleProfileViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        littleProfileViewModel.startQueryForPrizesSet()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_your_set_prizes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addYourSetPrizesToRecycler(view)
    }

    /**
     * Makes sure the query task is completed before continuing
     */
    private fun addYourSetPrizesToRecycler(view: View) {
        if (littleProfileViewModel.hasLoadedPrizesSet()) {
            attachPrizesSetAdapter(view)
        } else {
            val prizesSetQueryTask = littleProfileViewModel.getPrizesSetQuery()!!
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
        view.yourSetPrizesRecyclerView.adapter = littleProfileViewModel.getPrizesSetAdapter()
        if (littleProfileViewModel.getPrizesSetAdapter().itemCount == 0) {
            view.no_set_prizes_image.visibility = View.VISIBLE
        } else {
            view.no_set_prizes_image.visibility = View.INVISIBLE
        }
    }

    /**
     * Gathers all the prizes set and sets up the recyclerview to place them in
     */
    private fun handlePrizesSetQueryTask(prizesSetQueryTask: Task<QuerySnapshot>, view: View) {
        littleProfileViewModel.compilePrizesSet(prizesSetQueryTask.result!!)
        littleProfileViewModel.createPrizesSetAdapter(view)
        attachPrizesSetAdapter(view)
    }
}