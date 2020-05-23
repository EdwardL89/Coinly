package com.eightnineapps.coinly.classes

import androidx.recyclerview.widget.RecyclerView

class PrizeLoader {

    /**
     * Retrieves the images of all the prizes given by this user from storage
     */
    fun loadAllPrizesGiven(recyclerView: RecyclerView) {
        recyclerView.removeAllViews()
    }

    /**
     * Retrieves the images of all the prizes claimed by this user from storage
     */
    fun loadAllPrizesClaimed(recyclerView: RecyclerView) {
        recyclerView.removeAllViews()
    }

}