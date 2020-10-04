package com.eightnineapps.coinly.viewmodels.activityviewmodels.profiles

import android.content.Context
import androidx.lifecycle.ViewModel
import com.eightnineapps.coinly.adapters.PrizesRecyclerViewAdapter
import com.eightnineapps.coinly.classes.objects.Prize
import com.eightnineapps.coinly.classes.objects.User
import com.eightnineapps.coinly.enums.PrizeTapLocation
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot

class BigProfileViewModel: ViewModel() {

    lateinit var observedUserInstance: User
    private var hasLoadedPrizesSet = false
    private var hasLoadedPrizesClaimed = false
    private val allPrizesSet = mutableListOf<Prize>()
    private val allPrizesClaimed = mutableListOf<Prize>()
    private var prizesSetQuery: Task<QuerySnapshot>? = null
    private var prizesClaimedQuery: Task<QuerySnapshot>? = null
    private var prizesSetAdapter: PrizesRecyclerViewAdapter? = null
    private var prizesClaimedAdapter: PrizesRecyclerViewAdapter? = null

    /**
     * Determines whether or not the prizes set have been loaded
     */
    fun hasLoadedPrizesSet() = hasLoadedPrizesSet

    /**
     * Determines whether or not the prizes claimed have been loaded
     */
    fun hasLoadedPrizesClaimed() = hasLoadedPrizesClaimed

    /**
     * Returns the adapter of the prizes set recycler view
     */
    fun getPrizesSetAdapter() = prizesSetAdapter!!

    /**
     * Returns the adapter of the prizes claimed recycler view
     */
    fun getPrizesClaimedAdapter() = prizesClaimedAdapter!!

    /**
     * Returns the query to get the prizes set
     */
    fun getPrizesSetQuery() = prizesSetQuery

    /**
     * Returns the query to get the prizes claimed
     */
    fun getPrizesClaimedQuery() = prizesClaimedQuery

    /**
     * Instantiates the adapter for the prizes set recycler
     */
    fun createPrizesSetAdapter() {
        prizesSetAdapter = PrizesRecyclerViewAdapter(allPrizesSet,
            PrizeTapLocation.BIG_PRIZES_SET, observedUserInstance)
    }

    /**
     * Instantiates the adapter for the prizes claimed recycler
     */
    fun createPrizesClaimedAdapter() {
        prizesClaimedAdapter = PrizesRecyclerViewAdapter(allPrizesClaimed,
            PrizeTapLocation.BIG_PRIZES_CLAIMED, observedUserInstance)
    }

    /**
     * Launches the query to get the prizes set
     */
    fun startQueryForPrizesSet(): Task<QuerySnapshot> {
        return Firestore.getPrizesSet(CurrentUser.getEmail()!!, observedUserInstance.email!!).get()
    }

    /**
     * Launches the query to get the prizes claimed
     */
    fun startQueryForClaimedPrizes(): Task<QuerySnapshot> {
        return Firestore.getPrizesClaimed(CurrentUser.getEmail()!!, observedUserInstance.email!!).get()
    }

    /**
     * Saves all the prizes set retrieved from the query
     */
    fun compilePrizesSet(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allPrizesSet.add(document.toObject(Prize::class.java))
        }
        hasLoadedPrizesSet = true
    }

    /**
     * Saves all the prizes claimed retrieved from the query
     */
    fun compilePrizesClaimed(querySnapshot: QuerySnapshot) {
        for (document in querySnapshot) {
            allPrizesClaimed.add(document.toObject(Prize::class.java))
        }
        hasLoadedPrizesClaimed = true
    }

    /**
     * Removes the observed Big and navigates to the previous page
     */
    fun removeBigAndSendBack(context: Context) {
//        CurrentUser.bigToBeRemoved = observedUserInstance
//
//        CurrentUser.decrementBigs()
//        Firestore.update(currentUserInstance!!, "numOfBigs", currentUserInstance.numOfBigs.toString())
//        Firestore.removeBig(currentUserInstance.email!!, observedUserInstance.email!!)
//
//        observedUserInstance.numOfLittles -= 1
//        Firestore.update(observedUserInstance, "numOfLittles", observedUserInstance.numOfLittles.toString())
//        Firestore.removeLittle(observedUserInstance.email!!, currentUserInstance.email!!)
//        CurrentUser.numOfBigs.value = currentUserInstance.numOfBigs
//
//        Toast.makeText(context, "Removed ${observedUserInstance.displayName} as a big", Toast.LENGTH_SHORT).show()
//
//        (context as Activity).finish()
    }
}