package com.eightnineapps.coinly.classes

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

abstract class PrizeLoader() : AppCompatActivity() {

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