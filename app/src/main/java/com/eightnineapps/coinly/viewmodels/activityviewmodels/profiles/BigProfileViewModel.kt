package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import kotlinx.android.synthetic.main.fragment_big_profile.*

class BigProfileViewModel: ViewModel() {

    private lateinit var setPrizesRecyclerView: RecyclerView
    private lateinit var claimedPrizesRecyclerView: RecyclerView
    lateinit var observedUserInstance: User
    val currentUserInstance = CurrentUser.instance

    /**
     * Removes the observed Big and navigates to the previous page
     */
    fun removeBigAndSendBack(context: Context) {
        currentUserInstance!!.numOfBigs -= 1
        Firestore.update(currentUserInstance!!, "numOfBigs", currentUserInstance.numOfBigs.toString())
        Firestore.removeBig(currentUserInstance.email!!, observedUserInstance.email!!)

        observedUserInstance.numOfLittles -= 1
        Firestore.update(observedUserInstance, "numOfLittles", observedUserInstance.numOfLittles.toString())
        Firestore.removeLittle(observedUserInstance.email!!, currentUserInstance.email!!)

        Toast.makeText(context, "Removed ${observedUserInstance.displayName} as a big", Toast.LENGTH_SHORT).show()
        
        (context as Activity).finish()
    }

    /**
     * Loads the prizes set by the big into the given recycler view
     */
    fun loadSetPrizes(recyclerView: RecyclerView, context: Context) {
        setPrizesRecyclerView = recyclerView
        setPrizesRecyclerView.removeAllViews()
        updateSetPrizesRecyclerViewAdapterAndLayoutManager(context)
    }

    /**
     * Loads the prizes claimed by the little into the given recycler view
     */
    fun loadClaimedPrizes(recyclerView: RecyclerView, context: Context) {
        claimedPrizesRecyclerView = recyclerView
        claimedPrizesRecyclerView.removeAllViews()
        updateClaimedPrizesRecyclerViewAdapterAndLayoutManager(context)
    }

    private fun updateClaimedPrizesRecyclerViewAdapterAndLayoutManager(context: Context) {
        claimedPrizesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Firestore.getPrizesClaimed(currentUserInstance!!.email!!, observedUserInstance.email!!).get().addOnSuccessListener {
            val allPrizesClaimed = mutableListOf<Prize>()
            for (document in it) {
                allPrizesClaimed.add(document.toObject(Prize::class.java))
            }
            claimedPrizesRecyclerView.adapter = PrizesRecyclerViewAdapter(allPrizesClaimed,
                context, PrizeTapLocation.BIG_PRIZES_CLAIMED, currentUserInstance, observedUserInstance)
            if (allPrizesClaimed.isNotEmpty()) {
                (context as Activity).no_prizes_claimed_image.visibility = View.INVISIBLE
            } else {
                (context as Activity).no_prizes_claimed_image.visibility = View.VISIBLE
            }
        }
    }

    /**
     * Assigns the given recycler view's layout manager and adapter using the list whose data is being displayed
     */
    private fun updateSetPrizesRecyclerViewAdapterAndLayoutManager(context: Context?) {
        setPrizesRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        Firestore.getPrizesSet(currentUserInstance!!.email!!, observedUserInstance.email!!).get().addOnSuccessListener {
            val allPrizesSet = mutableListOf<Prize>()
            for (document in it) {
                allPrizesSet.add(document.toObject(Prize::class.java))
            }
            setPrizesRecyclerView.adapter = PrizesRecyclerViewAdapter(allPrizesSet, context!!, PrizeTapLocation.BIG_PRIZES_SET, currentUserInstance, observedUserInstance)
            if (allPrizesSet.isNotEmpty()) {
                (context as Activity).no_prizes_set_by_big_image.visibility = View.INVISIBLE
            } else {
                (context as Activity).no_prizes_set_by_big_image.visibility = View.VISIBLE
            }
        }
    }
}