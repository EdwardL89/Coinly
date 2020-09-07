package com.eightnineapps.coinly.viewmodels.fragmentviewmodels

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.eightnineapps.coinly.models.CurrentUser
import com.eightnineapps.coinly.models.Firestore
import com.firebase.ui.auth.data.model.User

class AllBigsFragmentViewModel: TabLayoutFragmentViewModel() {

    private var hasLoadedData = false

    fun addAllBigsToRecyclerView(recyclerView: RecyclerView, context: Context?) {
        setupRecycler(recyclerView)
        addSpaceBetweenItems(context)
        if (!hasLoadedData) {
            Firestore.getBigs(CurrentUser.instance!!.email!!).get().addOnSuccessListener {
                val bigs = mutableListOf<String>()
                for (doc in it) {
                    bigs.add(doc["email"].toString())
                }
                addUsersToRecyclerView(bigs, context)
                hasLoadedData = true
            }
        } else {
            updateRecyclerViewAdapterAndLayoutManager(context)
        }
    }

}